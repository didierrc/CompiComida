package com.example.compicomida

import android.app.Activity
import android.app.Application
import android.app.DatePickerDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import android.icu.util.Calendar
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.example.compicomida.CompiComidaApp.Companion.CANAL_SIMPLE
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.model.localDb.LocalDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs

class CompiComidaApp : Application() {

    companion object {
        const val DEFAULT_GROCERY_URI =
            "https://cdn-icons-png.flaticon.com/512/1261/1261163.png"
        lateinit var appModule: AppModule
        const val RECIPES_COLLECTION = "recipes"
        const val RECIPES_COLLECTION_EN = "recipes_en"
        const val HOME_NUMBER_TABS = 3
        const val TODAY_FILTER = "TODAY"
        const val TOMORROW_FILTER = "TOMORROW"
        const val TWO_DAYS_FILTER = "2-DAYS"
        val MAPS_API_KEY: String
            get() = BuildConfig.MAPS_API_KEY
        const val  CANAL_SIMPLE = "Canal simple"
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val idCanal = CANAL_SIMPLE
            val nombreCanal = "Notifications"
            val importancia = NotificationManager.IMPORTANCE_DEFAULT
            val canal = NotificationChannel(idCanal,nombreCanal,importancia)
            canal.description = "Channel for notifications"

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }
}

interface AppModule {
    val localDb: LocalDatabase
    val recipesDb: FirebaseFirestore
    val pantryRepo: PantryRepository
    val groceryRepo: GroceryRepository
    val recipesRepo: RecipeRepository
    val preferencesDailyRecipe: PreferencesDailyRecipe

    fun parseExpirationDate(expirationDate: LocalDateTime): String
    fun parseUnitQuantity(unit: String?, quantity: Double): String
    fun createDatePicker(
        context: Context,
        dateTInput: TextInputEditText
    ): DatePickerDialog

    fun parseQuantity(quantity: Double): String
    fun parseLastUpdate(lastUpdate: LocalDateTime): String
    fun getDateDifference(dateFirst: LocalDate, dateSecond: LocalDate): Long
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
    override val preferencesDailyRecipe: PreferencesDailyRecipe
        get() = PreferencesDailyRecipe(context.dataStore)

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
        val unitParsed =
            if (unit != context.getString(R.string.item_default_unit) && unit != null) unit else ""
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
        return context.getString(
            R.string.grocery_list_last_update,
            formattedDate
        )
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

    override fun getDateDifference(dateFirst: LocalDate, dateSecond: LocalDate): Long {
        return abs(
            Duration.between(dateFirst.atStartOfDay(), dateSecond.atStartOfDay())
                .toDays()
        )
    }
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()