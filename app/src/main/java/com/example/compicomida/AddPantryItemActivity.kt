package com.example.compicomida

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.compicomida.databinding.ActivityAddPantryItemBinding
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.converters.DateConverter
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.PantryItem
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class AddPantryItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPantryItemBinding
    private lateinit var db: LocalDatabase

    private lateinit var itemName: TextInputEditText
    private lateinit var quantity: TextInputEditText
    private lateinit var units: AutoCompleteTextView
    private lateinit var expirationDate: TextInputEditText
    private lateinit var btnAdd: Button
    private lateinit var btnImage: Button
    private var imageURI: String? = null

    private val calendar = Calendar.getInstance()





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

        db = LocalDatabase.getDB(this)

        initialiseViewElements()

        // Init db
        db.let {
            addOnClickListener(it)
        }
        setSupportActionBar(binding.toolbarAddPantry)
        binding.toolbarAddPantry.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initialiseViewElements() {
        itemName = binding.etPantryName
        quantity = binding.etPantryQuantity
        units = binding.spinnerProductUnitsAddPantry
        expirationDate = binding.etProductExpirationDateAddPantry
        btnAdd = binding.btAddPantryItem
        btnImage = binding.btnImgAddPantry

        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Formatear la fecha seleccionada
                val formattedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                expirationDate.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        expirationDate.setOnClickListener {
            datePicker.show()
        }

    }

    private fun addOnClickListener(db: LocalDatabase) {
        btnAdd.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {

                val itemNameTxt = itemName.text.toString().trim()
                val quantityTxt = quantity.text.toString().trim()
                val unitTxt = units.text.toString().trim()
                val expirationDateTxt = expirationDate.text.toString().trim()

                val quantityValue = quantityTxt.toDoubleOrNull()
                if (itemNameTxt.isBlank() || unitTxt.isBlank()) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_empty_fields_add_grocery_item))
                    }
                } else if ( quantityValue == null) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_valid_numbers_add_pantry_item))
                    }
                } else if ( expirationDateTxt.isBlank()) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_expiration_date_not_found_add_pantry_item))
                    }
                } else {
                    val expirationDateValue = DateConverter().fromTimestampWithOutHours(expirationDateTxt)
                    val newItem = PantryItem(
                        pantryId =  0,
                        itemId = null,
                        expirationDate = expirationDateValue!!,
                        pantryName =  itemNameTxt,
                        quantity = quantityValue,
                        unit = unitTxt,
                        lastUpdate = LocalDateTime.now(),
                        pantryPhotoUri = imageURI
                            ?: "https://cdn-icons-png.flaticon.com/512/1261/1261163.png"
                    )

                    db.pantryItemDao().add(newItem)
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
                }
            }

        btnImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply { type = "image/*" }
            imagePickerLauncher.launch(intent)
        }
    }

    private fun showAlert(message: String, title: String = "Error") {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}