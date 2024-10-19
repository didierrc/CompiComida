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

            db!!.groceryListDao().add(GroceryList(1, "Prueba 1-N", Date()))

            val listInserted = db!!.groceryListDao().getAll().first()
            val listItems = listOf(
                GroceryItem(
                    itemId = 0,
                    listId = listInserted.listId,
                    itemName = "Manzanas",
                    expirationDate = Date(),
                    quantity = 10,
                    unit = null,
                    price = 2.0,
                    isPurchased = false,
                    itemPhotoUri = "pp.jpg"
                ),
                GroceryItem(
                    itemId = 0,
                    listId = listInserted.listId,
                    itemName = "Mantequilla",
                    expirationDate = Date(),
                    quantity = 10,
                    unit = "gr",
                    price = 10.0,
                    isPurchased = true,
                    itemPhotoUri = "gg.jpg"
                )
            )

            db!!.groceryItemDao().addAll(*listItems.toTypedArray())

            db!!.groceryListDao().getAllWithItems().forEach { entry ->
                println("Nombre Lista: ${entry.key.listName}")
                println("Productos:")
                entry.value.forEach {
                    println("- ${it.itemName}: ")
                }
            }


        }


    }
}