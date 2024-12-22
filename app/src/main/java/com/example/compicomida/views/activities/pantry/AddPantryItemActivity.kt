package com.example.compicomida.views.activities.pantry

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.CompiComidaApp
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
    private var selectedUnit: String? = null // Para guardar la selección

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
        initAutoCompleteTextView()
    }

    // Guardar el estado al rotar la pantalla
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedUnit", selectedUnit)
    }

    // Restaurar el estado al recrear la actividad
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val units = resources.getStringArray(R.array.grocery_item_units)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, units)
        binding.spinnerProductUnitsAddPantry.setAdapter(adapter)


        selectedUnit = savedInstanceState.getString("selectedUnit")
        selectedUnit?.let {
            binding.spinnerProductUnitsAddPantry.setText(it, false)
        }
    }

    private fun initAutoCompleteTextView(){
        val units = resources.getStringArray(R.array.grocery_item_units)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, units)
        binding.spinnerProductUnitsAddPantry.setAdapter(adapter)

        binding.spinnerProductUnitsAddPantry.setOnItemClickListener { _, _, position, _ ->
            selectedUnit = adapter.getItem(position)
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
                    val intent = Intent()
                    setResult(Activity.RESULT_OK, intent)
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
                val intent =
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
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


}