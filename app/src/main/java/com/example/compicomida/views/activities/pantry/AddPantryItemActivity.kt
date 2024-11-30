package com.example.compicomida.views.activities.pantry

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityAddPantryItemBinding
import com.example.compicomida.model.localDb.converters.DateConverter
import com.example.compicomida.model.localDb.entities.PantryItem
import com.example.compicomida.viewmodels.AddPantryItemViewModel
import com.example.compicomida.viewmodels.factories.AddPantryItemViewModelFactory
import java.time.LocalDateTime
import java.util.Locale

class AddPantryItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPantryItemBinding
    private lateinit var addPantryItemViewModel: AddPantryItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPantryItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.addPantryItem)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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
            AddPantryItemViewModelFactory(CompiComidaApp.appModule.pantryRepo)
        )[AddPantryItemViewModel::class.java]

        initialiseCalendarElement()
        initialiseAddOnClick()
    }

    private fun initialiseCalendarElement() {

        val calendar = Calendar.getInstance()

        with(binding) {

            val datePicker = DatePickerDialog(
                root.context,
                { _, year, month, dayOfMonth ->
                    val formattedDate = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%04d", dayOfMonth, month + 1, year
                    )
                    etProductExpirationDateAddPantry.setText(formattedDate) // setText of expiration date Text View
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            etProductExpirationDateAddPantry.setOnClickListener {
                datePicker.show()
            }


        }
    }

    private fun initialiseAddOnClick() {

        with(binding) {
            btAddPantryItem.setOnClickListener {

                val itemNameTxt = etPantryName.text.toString().trim()
                val quantityTxt = etPantryQuantity.text.toString().trim()
                val unitTxt = spinnerProductUnitsAddPantry.text.toString().trim()
                val expirationDateTxt = etProductExpirationDateAddPantry.text.toString().trim()
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

                    // Notifying fragment up
                    val resultIntent = Intent()
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }


            }

            val imagePickerLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        addPantryItemViewModel.updateImage(result.data?.data.toString())
                    }
                }

            btnImgAddPantry.setOnClickListener {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
                imagePickerLauncher.launch(intent)
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
            showAlert(getString(R.string.error_empty_fields_add_grocery_item))
            add = false
        } else if (quantityValue == null) {
            showAlert(getString(R.string.error_valid_numbers_add_pantry_item))
            add = false
        } else if (expirationDateTxt.isBlank()) {
            showAlert(getString(R.string.error_expiration_date_not_found_add_pantry_item))
            add = false
        } else {
            val expirationDateValue =
                DateConverter().fromTimestampWithOutHours(expirationDateTxt)

            if (expirationDateValue!!.isBefore(LocalDateTime.now())) {
                showAlert(getString(R.string.error_valid_expiration_date_add_pantry_item))
                add = false
            }
        }

        return add
    }

    // Shows a generic alert message
    private fun showAlert(message: String, title: String = "Error") {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}