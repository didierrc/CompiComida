package com.example.compicomida

import android.app.Application
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.icu.util.Calendar
import androidx.appcompat.app.AlertDialog
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.model.localDb.LocalDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CompiComidaApp : Application() {

    companion object {
        const val DEFAULT_GROCERY_URI =
            "https://cdn-icons-png.flaticon.com/512/1261/1261163.png"
        lateinit var appModule: AppModule
        const val RECIPES_COLLECTION = "recipes"
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}

interface AppModule {
    val localDb: LocalDatabase
    val recipesDb: FirebaseFirestore
    val pantryRepo: PantryRepository
    val groceryRepo: GroceryRepository
    val recipesRepo: RecipeRepository
    fun parseExpirationDate(expirationDate: LocalDateTime): String
    fun parseUnitQuantity(unit: String?, quantity: Double): String
    fun createDatePicker(
        context: Context,
        dateTInput: TextInputEditText
    ): DatePickerDialog

    fun parseQuantity(quantity: Double): String
    fun parseLastUpdate(lastUpdate: LocalDateTime): String
    fun showAlert(context: Context, message: String, title: String = "Error")
}

class AppModuleImpl(
    private val context: Context
) : AppModule {

    // Repository - Pantry, Grocery, Recipes, GroceryLists
    override val pantryRepo: PantryRepository by lazy {
        PantryRepository(localDb)
    }

    override val groceryRepo: GroceryRepository by lazy {
        GroceryRepository(localDb)
    }

    override val recipesRepo: RecipeRepository by lazy {
        RecipeRepository(recipesDb)
    }

    // Room - Local Database initialisation
    override val localDb: LocalDatabase by lazy {
        LocalDatabase.getDB(context)
    }

    // Firestore - Firebase initialisation
    override val recipesDb: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    // ---- UTIL functions ---- Could have other class with just static functions

    // Transform a LocalDateTime to "Caduca el dd/mm/yyyy"
    override fun parseExpirationDate(expirationDate: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = expirationDate.format(formatter)
        return formattedDate
    }

    // Transform a unit and a quantity to "Cantidad: quantity unidad"
    override fun parseUnitQuantity(unit: String?, quantity: Double): String {

        // If unit is "No especificada" or NULL --> ""
        // If unit is other --> unit
        val unitParsed = if (unit != "No especificada" && unit != null) unit else ""
        val quantityParsed = parseQuantity(quantity)
        return context.getString(
            R.string.grocery_items_adapter_cantidad_text,
            quantityParsed,
            unitParsed
        )
    }

    override fun parseQuantity(quantity: Double): String {
        // If quantity has no decimal --> to Integer (except for 0, not shown)
        // If unit has decimal --> as it is (except for 0.5, shown as 1/2)
        // other fractions can be considered...
        val quantityParsed = if (quantity.mod(1.0) == 0.0) {
            if (quantity.toInt() == 0) "-" else quantity.toInt().toString()
        } else {
            if (quantity == 0.5)
                "1/2"
            else
                quantity.toString()
        }
        return quantityParsed
    }

    override fun createDatePicker(
        context: Context,
        dateTInput: TextInputEditText
    ): DatePickerDialog {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val formattedDate = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
                )
                dateTInput.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        return datePicker
    }

    override fun parseLastUpdate(lastUpdate: LocalDateTime): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val formattedDate = lastUpdate.format(formatter)
        return "Última actualización el $formattedDate"
    }

    // Shows a generic alert message
    override fun showAlert(context: Context, message: String, title: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()