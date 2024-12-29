package com.example.compicomida.views.activities.pantry

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.CompiComidaApp.Companion.DEFAULT_GROCERY_URI
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityAddPantryItemBinding
import com.example.compicomida.model.localDb.converters.DateConverter
import com.example.compicomida.model.localDb.entities.PantryItem
import com.example.compicomida.viewmodels.pantry.AddPantryItemViewModel
import com.example.compicomida.viewmodels.pantry.factory.AddPantryItemViewModelFactory
import java.time.LocalDateTime

class AddPantryItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPantryItemBinding
    private lateinit var addPantryItemViewModel: AddPantryItemViewModel
    private val appModule = CompiComidaApp.appModule

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPantryItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addPantryItem)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Back button
        setSupportActionBar(binding.toolbarAddPantry)
        binding.toolbarAddPantry.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Initialising the view model
        addPantryItemViewModel = ViewModelProvider(
            this,
            AddPantryItemViewModelFactory(appModule.pantryRepo)
        )[AddPantryItemViewModel::class.java]

        // Initialising DatePicker
        with(binding) {
            etProductExpirationDateAddPantry.setOnClickListener {
                appModule.createDatePicker(
                    this@AddPantryItemActivity,
                    etProductExpirationDateAddPantry
                ).show()
            }
        }

        initialiseAddOnClick()

        addPantryItemViewModel.image.observe(this) {
            if (it == DEFAULT_GROCERY_URI)
                showImagePreview(null)
            else
                showImagePreview(Uri.parse(it))
        }
    }

    private fun initialiseAddOnClick() {

        with(binding) {
            btAddPantryItem.setOnClickListener {

                val itemNameTxt = etPantryName.text.toString().trim()
                val quantityTxt = etPantryQuantity.text.toString().trim()
                val unitTxt = spinnerProductUnitsAddPantry.text.toString().trim()
                val expirationDateTxt =
                    etProductExpirationDateAddPantry.text.toString().trim()
                val quantityValue = quantityTxt.toDoubleOrNull()

                if (canAddPantry(
                        itemNameTxt,
                        unitTxt,
                        quantityValue,
                        expirationDateTxt
                    )
                ) {

                    // Already validated
                    val dateToPantry =
                        DateConverter().fromTimestampWithOutHours(expirationDateTxt)!!

                    val newItem = PantryItem(
                        pantryId = 0,
                        itemId = null,
                        expirationDate = dateToPantry,
                        pantryName = itemNameTxt,
                        quantity = quantityValue!!, // Already validated
                        unit = unitTxt,
                        lastUpdate = LocalDateTime.now(),
                        pantryPhotoUri = addPantryItemViewModel.image.value
                    )

                    addPantryItemViewModel.addPantryItem(newItem)

                    // Persist permission for the image URI, so it can be shown later in the list
                    contentResolver.takePersistableUriPermission(
                        addPantryItemViewModel.image.value?.let { Uri.parse(it) }!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    setResult(Activity.RESULT_OK)
                    finish()
                }


            }

            val imagePickerLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        addPantryItemViewModel.updateImage(result.data?.data.toString())
                    }
                }

            btnImg.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
                imagePickerLauncher.launch(intent)
            }

            binding.btnRemoveImage.setOnClickListener {
                binding.btnRemoveImage.animate().alpha(0f).setDuration(250)
                    .withEndAction {
                        addPantryItemViewModel.updateImage(DEFAULT_GROCERY_URI)
                    }.start()
            }


        }


    }

    private fun canAddPantry(
        itemNameTxt: String,
        unitTxt: String,
        quantityValue: Double?,
        expirationDateTxt: String
    ): Boolean {

        var add = true

        if (itemNameTxt.isBlank() || unitTxt.isBlank()) {
            appModule.showAlert(
                this,
                getString(R.string.error_empty_fields_add_grocery_item)
            )
            add = false
        } else if (quantityValue == null) {
            appModule.showAlert(
                this,
                getString(R.string.error_valid_numbers_add_pantry_item)
            )
            add = false
        } else if (expirationDateTxt.isBlank()) {
            appModule.showAlert(
                this,
                getString(R.string.error_expiration_date_not_found_add_pantry_item)
            )
            add = false
        } else {
            val expirationDateValue =
                DateConverter().fromTimestampWithOutHours(expirationDateTxt)

            if (expirationDateValue!!.isBefore(LocalDateTime.now())) {
                appModule.showAlert(
                    this,
                    getString(R.string.error_valid_expiration_date_add_pantry_item)
                )
                add = false
            }
        }

        return add
    }

    private fun showImagePreview(uri: Uri?) {
        binding.ivImagePreview.setImageURI(uri)
        val visibility = if (uri != null) View.VISIBLE else View.GONE
        binding.ivImagePreview.visibility = visibility
        binding.btnRemoveImage.visibility = visibility
        rearrangeElements(uri)
    }

    private fun rearrangeElements(uri: Uri?) {
        val constraintLayout = binding.addPantryItem
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        if (uri != null) {
            // Reset alpha value
            // This is necessary since it's put to zero in the animation,
            // otherwise it will not be visible even if it's set to View.VISIBLE
            binding.btnRemoveImage.alpha = 1f

            constraintSet.connect(
                R.id.btAddPantryItem,
                ConstraintSet.TOP,
                R.id.iv_image_preview,
                ConstraintSet.BOTTOM,
                16
            )
        } else {
            constraintSet.connect(
                R.id.btAddPantryItem,
                ConstraintSet.TOP,
                R.id.btn_img,
                ConstraintSet.BOTTOM,
                16
            )
        }

        constraintSet.applyTo(constraintLayout)
    }


}