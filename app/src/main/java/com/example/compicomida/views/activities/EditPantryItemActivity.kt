package com.example.compicomida.views.activities

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityEditPantryItemBinding
import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.converters.DateConverter
import com.example.compicomida.model.localDb.entities.PantryItem
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EditPantryItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditPantryItemBinding
    private lateinit var db: LocalDatabase

    private lateinit var itemName: TextInputEditText
    private lateinit var quantity: TextInputEditText
    private lateinit var units: AutoCompleteTextView
    private lateinit var expirationDate: TextInputEditText
    private lateinit var lastUpdate: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var btnImage: Button
    private lateinit var imageView: ImageView
    private var imageURI: String? = null

    private val calendar = Calendar.getInstance()

    private var itemId: Int = -1
    private var pantryItem: PantryItem? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditPantryItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editPantryItem)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        itemId = intent.getIntExtra("pantryId", -1)

        db = LocalDatabase.getDB(this)

        initialiseViewElements()

        // Init db
        db.let {
            loadPantryItem(it)
            editOnClickListener(it)
            deleteOnClickListener(it)
        }
        setSupportActionBar(binding.toolbarEditPantry)
        binding.toolbarEditPantry.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun initialiseViewElements() {
        itemName = binding.etEditPantryName
        quantity = binding.etEditPantryQuantity
        units = binding.spinnerProductUnitsEditPantry
        expirationDate = binding.etProductExpirationDateEditPantry
        lastUpdate = binding.tvEditPantryItemLastUpdate
        btnEdit = binding.btEditPantryItem
        btnDelete = binding.btDeletePantryItem
        btnImage = binding.btnImgEditPantry
        imageView = binding.editPantryItemImage

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

    private fun loadPantryItem(db: LocalDatabase) {
        lifecycleScope.launch(Dispatchers.Default) {
            pantryItem = db.pantryItemDao.getById(itemId)

            if (pantryItem == null) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            } else {
                withContext(Dispatchers.Main) {
                    itemName.setText(pantryItem!!.pantryName)
                    quantity.setText(pantryItem!!.quantity.toString())
                    units.setText(pantryItem!!.unit)
                    expirationDate.setText(parseExpirationDate(pantryItem!!.expirationDate))
                    lastUpdate.text = parseLastUpdate(pantryItem!!.lastUpdate)
                    imageView.load(pantryItem!!.pantryPhotoUri)
                    imageURI = pantryItem!!.pantryPhotoUri
                }
            }
        }
    }

    private fun parseLastUpdate(lastUpdate: LocalDateTime): CharSequence {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = lastUpdate.format(formatter)
        return "Ultima actualización el $formattedDate"
    }

    private fun parseExpirationDate(expirationDate: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = expirationDate.format(formatter)
        return formattedDate
    }

    private fun deleteOnClickListener(db: LocalDatabase) {
        btnDelete.setOnClickListener {
            lifecycleScope.launch(Dispatchers.Default) {
                db.pantryItemDao.delete(pantryItem!!)
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun editOnClickListener(db: LocalDatabase) {
        btnEdit.setOnClickListener {
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
                } else if (quantityValue == null) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_valid_numbers_add_pantry_item))
                    }
                } else if (expirationDateTxt.isBlank()) {
                    withContext(Dispatchers.Main) {
                        showAlert(getString(R.string.error_expiration_date_not_found_add_pantry_item))
                    }
                } else {
                    val expirationDateValue =
                        DateConverter().fromTimestampWithOutHours(expirationDateTxt)
                    if (expirationDateValue!!.isBefore(LocalDateTime.now())) {
                        withContext(Dispatchers.Main) {
                            showAlert(getString(R.string.error_valid_expiration_date_add_pantry_item))
                        }
                    } else {
                        pantryItem!!.pantryName = itemNameTxt
                        pantryItem!!.expirationDate = expirationDateValue
                        pantryItem!!.quantity = quantityValue
                        pantryItem!!.unit = unitTxt
                        pantryItem!!.lastUpdate = LocalDateTime.now()
                        pantryItem!!.pantryPhotoUri =
                            imageURI ?: "https://cdn-icons-png.flaticon.com/512/1261/1261163.png"

                        db.pantryItemDao.update(pantryItem!!)
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                }
            }
        }
        val imagePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri = result.data?.data
                    imageURI = uri.toString()
                    imageView.load(imageURI)
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