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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

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

            val aList = GroceryList(0, "Didier buys groceries", Date())
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
                    expirationDate = Date(),
                    quantity = 3,
                    unit = null,
                    price = 10.0,
                    isPurchased = false,
                    itemPhotoUri = "apple.jpg"
                )
            )

            db!!.groceryListDao().getAllWithItems().forEach { entry ->
                println("Nombre Lista: ${entry.key.listName}")
                println("Productos:")
                entry.value.forEach {
                    val catName = db!!.itemCategoryDao().getById(it.categoryId!!)?.categoryName
                    println("- ${it.itemName}: ${it.quantity} | ${it.price}€ | ${it.expirationDate} | $catName")
                }
            }


        }


    }
}