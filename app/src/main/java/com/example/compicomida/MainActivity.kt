package com.example.compicomida

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList
import com.example.compicomida.db.entities.ItemCategory
import com.example.compicomida.db.entities.PantryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private var db: LocalDatabase? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        db = LocalDatabase.getDB(this)

        lifecycleScope.launch(Dispatchers.IO) {

            db!!.clearAllTables()

            val aList = GroceryList(0, "Didier buys groceries", LocalDateTime.now())
            val aCat = ItemCategory(0, "Fruits")

            db!!.groceryListDao().add(aList)
            db!!.itemCategoryDao().add(aCat)

            val aListDB = db!!.groceryListDao().getAll().first()
            val aCatDB = db!!.itemCategoryDao().getAll().first()

            db!!.groceryItemDao().add(
                GroceryItem(
                    itemId = 0,
                    listId = aListDB.listId,
                    categoryId = aCatDB.categoryId,
                    itemName = "Apple",
                    quantity = 3,
                    unit = null,
                    price = 10.0,
                    isPurchased = false,
                    itemPhotoUri = "apple.jpg"
                )
            )

            db!!.groceryListDao().getAllWithItems().forEach { entry ->
                println("----- Nombre Lista: ${entry.key.listName}")
                println("** Productos:")
                entry.value.forEach {
                    val catName = db!!.itemCategoryDao().getById(it.categoryId!!)?.categoryName
                    println("- ${it.itemName}: ${it.quantity} | ${it.price}€ | $catName")
                }
            }

            db!!.pantryItemDao().add(
                PantryItem(
                    pantryId = 0,
                    itemId = null, // not associated to any grocery item
                    expirationDate = LocalDateTime.now().plusDays(20),
                    pantryName = "Apples",
                    quantity = 10,
                    unit = null,
                    lastUpdate = LocalDateTime.now().plusDays(-20),
                    pantryPhotoUri = "apples.jpg"
                )
            )

            println("----- Despensa")
            db!!.pantryItemDao().getAll().forEach {
                println(
                    "- ${it.pantryName}: ${it.quantity} | ${
                        it.expirationDate.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                    }"
                )
            }

        }


    }
}