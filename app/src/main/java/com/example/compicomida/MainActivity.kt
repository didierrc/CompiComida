package com.example.compicomida

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList
import com.example.compicomida.db.entities.ItemCategory
import com.example.compicomida.db.entities.PantryItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    private var db: LocalDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        // Default behaviour for Main Activity
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

        // Set up bottom navigation
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        val navHostFragment = findNavController(R.id.fragmentContainerView)
        bottomNav.setupWithNavController(navHostFragment)

        // Initialize DB
        db = LocalDatabase.getDB(this)
        db?.let { dbTestValues(it) }
    }

    /*
    Test values for grocery lists and grocery.
     */
    private fun dbTestValues(db: LocalDatabase) {

        lifecycleScope.launch(Dispatchers.IO) {

            db.clearAllTables()

            val groceryLists = listOf(
                GroceryList(0, "Lista Didier", LocalDateTime.now()),
                GroceryList(0, "Lista Raúl", LocalDateTime.now()),
                GroceryList(0, "Lista Yago", LocalDateTime.now()),
            )
            db.groceryListDao().addAll(*groceryLists.toTypedArray())

            val aCategory = ItemCategory(0, "Fruta")
            val aCategory2 = ItemCategory(0, "Verdura")
            val aCategory3 = ItemCategory(0, "Carne")
            val aCategory4 = ItemCategory(0, "Pescado")
            val aCategory5 = ItemCategory(0, "Lácteo")
            val aCategory6 = ItemCategory(0, "Bebida")
            val aCategory7 = ItemCategory(0, "Fruto Seco")
            db.itemCategoryDao().add(aCategory)
            db.itemCategoryDao().add(aCategory2)
            db.itemCategoryDao().add(aCategory3)
            db.itemCategoryDao().add(aCategory4)
            db.itemCategoryDao().add(aCategory5)
            db.itemCategoryDao().add(aCategory6)
            db.itemCategoryDao().add(aCategory7)
            val catId = db.itemCategoryDao().getAll().first().categoryId

            db.groceryListDao().getAll().forEach {
                val groceryItems = listOf(
                    GroceryItem(
                        0,
                        it.listId,
                        catId,
                        "Manzanas",
                        3,
                        "kg",
                        10.0,
                        false,
                        "https://cdn-icons-png.flaticon.com/512/740/740922.png"
                    ),
                    GroceryItem(
                        0,
                        it.listId,
                        catId,
                        "Plátanos",
                        2,
                        "kg",
                        5.0,
                        false,
                        "https://cdn-icons-png.flaticon.com/512/5779/5779223.png"
                    ),
                    GroceryItem(
                        0,
                        it.listId,
                        catId,
                        "Naranjas",
                        1,
                        "kg",
                        8.0,
                        false,
                        "https://cdn-icons-png.flaticon.com/512/721/721098.png"
                    ),
                )
                db.groceryItemDao().addAll(*groceryItems.toTypedArray())
            }

            val pantryItems = listOf(
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now(),
                    "Peras",
                    3,
                    null,
                    LocalDateTime.now().plusDays(-1),
                    "peras.jpg"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(5),
                    "Mangos",
                    2,
                    null,
                    LocalDateTime.now().plusDays(-2),
                    "mangos.jpg"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(7),
                    "Fresas",
                    1,
                    null,
                    LocalDateTime.now().plusDays(-3),
                    "fresas.jpg"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(10),
                    "Limones",
                    5,
                    null,
                    LocalDateTime.now().plusDays(-4),
                    "limones.jpg"
                )
            )
            db.pantryItemDao().addAll(*pantryItems.toTypedArray())
        }
    }


}