package com.example.compicomida.views.activities.pantry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import coil3.load
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityEditPantryItemBinding
import com.example.compicomida.model.localDb.converters.DateConverter
import com.example.compicomida.viewmodels.pantry.EditPantryItemViewModel
import com.example.compicomida.viewmodels.pantry.factory.EditPantryItemViewModelFactory
import com.example.compicomida.viewmodels.pantry.uiData.PantryItemUI
import java.time.LocalDateTime

class EditPantryItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditPantryItemBinding
    private lateinit var editPantryItemViewModel: EditPantryItemViewModel
    private val appModule = CompiComidaApp.appModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditPantryItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editPantryItem)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Lógica personalizada para el evento "Atrás"
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        })

        // Back Button
        setSupportActionBar(binding.toolbarEditPantry)
        binding.toolbarEditPantry.setNavigationOnClickListener {
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        // Initialising the view model
        editPantryItemViewModel = ViewModelProvider(
            this,
            EditPantryItemViewModelFactory(
                appModule.pantryRepo,
                intent.getIntExtra("pantryId", -1),
                resources.getStringArray(R.array.grocery_item_units)
            )
        )[EditPantryItemViewModel::class.java]


        // Initialising DatePicker
        with(binding) {
            etProductExpirationDateEditPantry.setOnClickListener {
                appModule.createDatePicker(
                    this@EditPantryItemActivity,
                    etProductExpirationDateEditPantry
                ).show()
            }
        }



        observePantryItem()
        observeImagePicker()
        editOnClickListener()
        deleteOnClickListener()
        initSpinnerUnits()
    }

    private fun observePantryItem() {
        editPantryItemViewModel.pantryItem.observe(this) { pantryItem ->

            // If pantry item does not exist, it goes back to the List
            if (pantryItem == null) {
                val intent = Intent()
                setResult(Activity.RESULT_OK, intent)
                finish()
            } else {
                with(binding) {
                    etEditPantryName.setText(pantryItem.pantryName)
                    etEditPantryQuantity.setText("${pantryItem.quantity}")
                    spinnerProductUnitsEditPantry.setHint(pantryItem.unit)
                    etProductExpirationDateEditPantry.setText(
                        appModule.parseExpirationDate(
                            pantryItem.expirationDate
                        )
                    )
                    tvEditPantryItemLastUpdate.text =
                        appModule.parseLastUpdate(pantryItem.lastUpdate)
                    editPantryItemImage.load(pantryItem.pantryPhotoUri)
                }
            }

        }
    }


    private fun observeImagePicker() {

        with(binding) {
            val imagePickerLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK)
                        editPantryItemViewModel.updateImage(result.data?.data.toString())
                }

            btnImgEditPantry.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
                imagePickerLauncher.launch(intent)
            }
        }

    }

    private fun deleteOnClickListener() {
        with(binding) {
            btDeletePantryItem.setOnClickListener {
                editPantryItemViewModel.deletePantryItem()
            }
        }
    }

    private fun editOnClickListener() {

        with(binding) {
            btEditPantryItem.setOnClickListener {

                val itemNameTxt = etEditPantryName.text.toString().trim()
                val quantityTxt = etEditPantryQuantity.text.toString().trim()
                val unitTxt = spinnerProductUnitsEditPantry.text.toString().trim()
                val expirationDateTxt =
                    etProductExpirationDateEditPantry.text.toString().trim()
                val quantityValue = quantityTxt.toDoubleOrNull()

                if (canEditPantry(
                        itemNameTxt,
                        unitTxt,
                        quantityValue,
                        expirationDateTxt
                    )
                ) {

                    val expirationDateValue =
                        DateConverter().fromTimestampWithOutHours(expirationDateTxt)
                    val editedPantryItem = PantryItemUI(
                        itemNameTxt,
                        expirationDateValue!!, // already checked
                        quantityValue!!, // already checked
                        unitTxt,
                        LocalDateTime.now()
                    )

                    editPantryItemViewModel.updatePantryItem(editedPantryItem)

                    Toast.makeText(
                        this@EditPantryItemActivity,
                        getString(R.string.edit_pantry_item_success),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        }

    }

    private fun canEditPantry(
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

    private fun initSpinnerUnits() {
        editPantryItemViewModel.updateUnits()
        editPantryItemViewModel.units.observe(this){
            binding.spinnerProductUnitsEditPantry.setAdapter(
                ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    it
                )
            )
        }
    }
}