package com.example.compicomida.views.activities.grocery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.CompiComidaApp.Companion.DEFAULT_GROCERY_URI
import com.example.compicomida.CompiComidaApp.Companion.appModule
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityAddGroceryItemBinding
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.viewmodels.AddGroceryItemViewModel
import com.example.compicomida.viewmodels.factories.AddGroceryItemViewModelFactory
import com.google.android.material.textfield.TextInputEditText

class AddGroceryItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddGroceryItemBinding
    private lateinit var itemName: TextInputEditText
    private lateinit var spinnerCategories: AutoCompleteTextView
    private lateinit var quantity: TextInputEditText
    private lateinit var units: AutoCompleteTextView
    private lateinit var price: TextInputEditText
    private lateinit var btnAdd: Button
    private lateinit var btnImage: ImageButton
    private var imageURI: String? = null

    private lateinit var addGroceryItemViewModel: AddGroceryItemViewModel

    private var listId: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddGroceryItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addGroceryItem)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        listId = intent.getIntExtra("listId", 0)

        // Initialising the view model
        addGroceryItemViewModel = ViewModelProvider(
            this,
            AddGroceryItemViewModelFactory(appModule.groceryRepo)
        )[AddGroceryItemViewModel::class.java]

        // Initialising the view elements
        initialiseViewElements()
        initSpinnerCategories()
        addOnClickListener()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        addGroceryItemViewModel.image.observe(this) {
            if (it == null)
                showImagePreview(null)
            else
                showImagePreview(Uri.parse(it))
        }

    }

    private fun initialiseViewElements() {
        itemName = findViewById(R.id.et_list_name)
        spinnerCategories = findViewById(R.id.spinner_product_type)
        quantity = findViewById(R.id.et_product_quantity)
        units = findViewById(R.id.spinner_product_units)
        price = findViewById(R.id.et_product_price)
        btnAdd = findViewById(R.id.btn_add_grocery_item)
        btnImage = findViewById(R.id.btn_img)

    }

    private fun addOnClickListener() {
        btnAdd.setOnClickListener {
            val itemCategory = addGroceryItemViewModel.itemCategory.value

            val listID = listId
            val categoryId = itemCategory?.categoryId
            val itemNameTxt = itemName.text.toString().trim()
            val quantityTxt = quantity.text.toString().trim()
            val unitTxt = units.text.toString().trim()
            val priceTxt = price.text.toString().trim()
            val priceValue = priceTxt.toDoubleOrNull()
            val quantityValue = quantityTxt.toDoubleOrNull()
            if (itemNameTxt.isBlank() || unitTxt.isBlank()) {
                appModule.showAlert(
                    this@AddGroceryItemActivity,
                    getString(R.string.error_empty_fields_add_grocery_item)
                )
            } else if (itemCategory == null) {
                appModule.showAlert(
                    this@AddGroceryItemActivity,
                    getString(R.string.error_category_not_found_add_grocery_item)
                )
            } else if (priceValue == null || quantityValue == null) {
                appModule.showAlert(
                    this@AddGroceryItemActivity,
                    getString(R.string.error_valid_numbers_add_grocery_item)
                )
            } else {
                val newItem = GroceryItem(
                    itemId = 0,
                    listId = listID,
                    categoryId = categoryId,
                    itemName = itemNameTxt,
                    quantity = quantityValue,
                    unit = unitTxt,
                    price = priceValue,
                    isPurchased = false,
                    itemPhotoUri = imageURI
                        ?: DEFAULT_GROCERY_URI
                )

                addGroceryItemViewModel.addGroceryItem(newItem)
                setResult(Activity.RESULT_OK)
                finish()
            }

        }
        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    imageURI = uri.toString()
                    addGroceryItemViewModel.updateImage(imageURI)
                }
            }

        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
            imagePickerLauncher.launch(intent)
        }

        binding.btnRemoveImage.setOnClickListener {
            binding.btnRemoveImage.animate().alpha(0f).setDuration(250).withEndAction {
                addGroceryItemViewModel.updateImage(null)
            }.start()
        }

        binding.spinnerProductUnits.setOnClickListener {
            addGroceryItemViewModel.updateItemCategory(spinnerCategories.text.toString())
        }
    }

    private fun showImagePreview(uri: Uri?) {
        binding.ivImagePreview.setImageURI(uri)
        val visibility = if (uri != null) View.VISIBLE else View.GONE
        binding.ivImagePreview.visibility = visibility
        binding.btnRemoveImage.visibility = visibility
        rearrangeElements(uri)
    }

    private fun rearrangeElements(uri: Uri?) {
        val constraintLayout = binding.addGroceryItem
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (uri != null) {
            // Reset alpha value
            // This is necessary since it's put to zero in the animation,
            // otherwise it will not be visible even if it's set to View.VISIBLE
            binding.btnRemoveImage.alpha = 1f

            constraintSet.connect(
                R.id.btn_add_grocery_item,
                ConstraintSet.TOP,
                R.id.iv_image_preview,
                ConstraintSet.BOTTOM,
                16
            )
        } else {
            constraintSet.connect(
                R.id.btn_add_grocery_item,
                ConstraintSet.TOP,
                R.id.btn_img,
                ConstraintSet.BOTTOM,
                16
            )
        }

        constraintSet.applyTo(constraintLayout)
    }

    private fun initSpinnerCategories() {
        addGroceryItemViewModel.updateCategories()
        addGroceryItemViewModel.categories.observe(this) {
            spinnerCategories.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
            )
        }
    }


}