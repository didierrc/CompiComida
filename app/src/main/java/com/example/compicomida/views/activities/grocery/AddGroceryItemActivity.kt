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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityAddGroceryItemBinding
import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private var listId: Int = 0
    private lateinit var db: LocalDatabase
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
        db = LocalDatabase.getDB(this)


        initialiseViewElements()

        // Init db
        db.let {
            // Initialise Spinner of Categories
            initSpinnerCategories(it)
            addOnClickListener(it)
        }
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
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

    private fun addOnClickListener(db: LocalDatabase) {
        btnAdd.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {

                // TODO: Here the category is found by the name in the spinner
                //  Research if there's a way of storing both ID and the Name in the Spinner
                //  so this is not necessary
                val itemCategory =
                    db.itemCategoryDao.getByName(spinnerCategories.text.toString())


                val listID = listId
                val categoryId = itemCategory?.categoryId
                val itemNameTxt = itemName.text.toString().trim()
                val quantityTxt = quantity.text.toString().trim()
                val unitTxt = units.text.toString().trim()
                val priceTxt = price.text.toString().trim()
                val priceValue = priceTxt.toDoubleOrNull()
                val quantityValue = quantityTxt.toDoubleOrNull()
                if (itemNameTxt.isBlank() || unitTxt.isBlank()) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_empty_fields_add_grocery_item))
                    }
                } else if (itemCategory == null) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_category_not_found_add_grocery_item))
                    }
                } else if (priceValue == null || quantityValue == null) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_valid_numbers_add_grocery_item))
                    }
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
                            ?: "https://cdn-icons-png.flaticon.com/512/1261/1261163.png"
                    )

                    db.groceryItemDao.add(newItem)
                    setResult(Activity.RESULT_OK)
                    finish()
                }


            }
        }
        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    imageURI = uri.toString()
                    showImagePreview(uri)

                }
            }

        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
            imagePickerLauncher.launch(intent)
        }

        binding.btnRemoveImage.setOnClickListener {
            binding.btnRemoveImage.animate().alpha(0f).setDuration(250).withEndAction {
                imageURI = null
                hideImagePreview()
            }.start()
        }
    }

    private fun showImagePreview(uri: Uri?) {
        binding.ivImagePreview.setImageURI(uri)
        val visibility = if (uri != null) View.VISIBLE else View.GONE
        binding.ivImagePreview.visibility = visibility
        binding.btnRemoveImage.visibility = visibility
        if (uri != null) {
            binding.btnRemoveImage.alpha = 1f // Reset alpha value
            // This is necessary since it's put to zero in the animation,
            // otherwise it will not be visible even if it's set to View.VISIBLE
        }
    }

    private fun hideImagePreview() {
        showImagePreview(null)
    }

    private fun showAlert(message: String, title: String = "Error") {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun initSpinnerCategories(db: LocalDatabase) {

        lifecycleScope.launch(Dispatchers.IO) {
            val categories = db.itemCategoryDao.getAll().map { it.categoryName }

            withContext(Dispatchers.Main) {
                spinnerCategories.setAdapter(
                    ArrayAdapter(
                        this@AddGroceryItemActivity,
                        android.R.layout.simple_spinner_dropdown_item,
                        categories
                    )
                )
            }


        }


    }


}