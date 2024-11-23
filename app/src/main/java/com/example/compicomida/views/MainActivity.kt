package com.example.compicomida.views

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.GroceryList
import com.example.compicomida.model.localDb.entities.ItemCategory
import com.example.compicomida.model.localDb.entities.PantryItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {

    // TODO: TEST PURPOSE ONLY. REMOVE FOR FINAL VERSION.
    private val db = CompiComidaApp.appModule.localDb

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up bottom navigation
        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation_view)
        val navHostFragment = findNavController(R.id.fragmentContainerView)
        bottomNav.setupWithNavController(navHostFragment)

        // TODO: TEST PURPOSE ONLY. REMOVE FOR FINAL VERSION.
        dbTestValues()
    }


    /*
    Test values for grocery lists and grocery.
     */
    private fun dbTestValues() {

        lifecycleScope.launch(Dispatchers.IO) {

            db.clearAllTables()

            val groceryLists = listOf(
                GroceryList(0, "Lista Didier", LocalDateTime.now()),
                GroceryList(0, "Lista Raúl", LocalDateTime.now()),
                GroceryList(0, "Lista Yago", LocalDateTime.now()),
            )
            db.groceryListDao.addAll(*groceryLists.toTypedArray())

            val aCategory = ItemCategory(0, "Fruta")
            val aCategory2 = ItemCategory(0, "Verdura")
            val aCategory3 = ItemCategory(0, "Carne")
            val aCategory4 = ItemCategory(0, "Pescado")
            val aCategory5 = ItemCategory(0, "Lácteo")
            val aCategory6 = ItemCategory(0, "Bebida")
            val aCategory7 = ItemCategory(0, "Fruto Seco")
            db.itemCategoryDao.add(aCategory)
            db.itemCategoryDao.add(aCategory2)
            db.itemCategoryDao.add(aCategory3)
            db.itemCategoryDao.add(aCategory4)
            db.itemCategoryDao.add(aCategory5)
            db.itemCategoryDao.add(aCategory6)
            db.itemCategoryDao.add(aCategory7)
            val catId = db.itemCategoryDao.getAll().first().categoryId

            db.groceryListDao.getAll().forEach {
                val groceryItems = listOf(
                    GroceryItem(
                        0,
                        it.listId,
                        catId,
                        "Manzanas",
                        3.0,
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
                        2.0,
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
                        1.0,
                        "kg",
                        8.0,
                        false,
                        "https://cdn-icons-png.flaticon.com/512/721/721098.png"
                    ),
                )
                db.groceryItemDao.addAll(*groceryItems.toTypedArray())
            }

            val pantryItems = listOf(
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now(),
                    "Peras",

                    3.0,
                    "",
                    LocalDateTime.now().plusDays(-1),
                    "https://cdn3.iconfinder.com/data/icons/fruits-52/150/icon_fruit_pera-512.png"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(1),
                    "Mangos",
                    3.0,
                    "kg",

                    LocalDateTime.now().plusDays(-2),
                    "https://cdn3.iconfinder.com/data/icons/fruits-52/150/icon_fruit_manga-256.png"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(2),
                    "Fresas",
                    200.0,
                    "g",
                    LocalDateTime.now().plusDays(-3),
                    "https://cdn3.iconfinder.com/data/icons/fruits-52/150/icon_fruit_morango-256.png"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(2),
                    "Limones",
                    6.0,
                    "",
                    LocalDateTime.now().plusDays(-4),
                    "https://cdn0.iconfinder.com/data/icons/fruity-3/512/Lemon-256.png"
                )
            )
            db.pantryItemDao.addAll(*pantryItems.toTypedArray())
        }
    }

}