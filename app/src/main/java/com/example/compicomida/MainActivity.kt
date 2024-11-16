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
import com.example.compicomida.db.entities.recipes.Ingredient
import com.example.compicomida.db.entities.recipes.Recipe
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
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

        // Generate recipes - Si se quiere añadir, cambiar el ID!!
        // generateRecipes()
    }

    private fun generateRecipes() {
        val firebaseRecipes = FirebaseFirestore.getInstance()

        val steps = listOf(
            "Lo primero que debes hacer para poder elaborar la receta fácil de ceviche peruano es exprimir los 12 limones para extraer su jugo, resérvalo para más adelante. Luego, corta el pescado en trozos de 3 centímetros, aproximadamente. Coloca los trozos en el recipiente que vayas a utilizar para servir el plato.",
            "Mezcla el pescado con el ají amarillo picado, el caldo de pescado y el cilantro o culantro. Coloca también la cebolla cortada en julianas y el ajo machacado. Déjalo reposar unos minutos para que se marine bien e se impregne de todos los sabores.",
            "Salpimienta al gusto y coloca a un lado del plato el camote sancochado, la lechuga y el maíz tierno también hervido.",
            "Después, vierte el zumo de limón por todo el plato de manera que quede todo bien cubierto. Por último, corta una lámina de ají limo rojo y colócala a modo de decoración. Resérvalo en el frigorífico para que se enfríe, puesto que este es un plato que se sirve frío. El ceviche peruano es un plato que actúa como aperitivo, por lo que puedes acompañarlo con un arroz con camarones o un delicioso salmón con salsa de gambas. ¡Listo para comer!"
        )

        val ingredients = listOf(
            Ingredient(
                name = "Pescado",
                quantity = 1.0,
                unit = "kg"
            ),
            Ingredient(
                name = "Cebolla",
                quantity = 1.0
            ),
            Ingredient(
                name = "Cilantro"
            ),
            Ingredient(
                name = "Ají amarillo",
                quantity = 1.0
            ),
            Ingredient(
                name = "Ají limo",
                quantity = 1.0
            ),
            Ingredient(
                name = "Limones",
                quantity = 12.0
            ),
            Ingredient(
                name = "Caldo de Pescado"
            ), Ingredient(
                name = "Sal"
            ),
            Ingredient(
                name = "Pimienta negra",
            ), Ingredient(
                name = "Maíz",
                quantity = 1.0
            ),
            Ingredient(
                name = "Boniato",
                quantity = 1.0
            ),
            Ingredient(
                name = "Lechuga",
                quantity = 1.0
            )
        )

        val documentId = "7"

        val recipe = Recipe(
            id = documentId,
            name = "Ceviche",
            preparationTime = 30,
            difficulty = 2,
            imageUrl = "https://imag.bonviveur.com/ceviche-peruano-de-pescado.jpg",
            category = "Recetas de Pescado",
            diner = 4,
            description = "El ceviche es un plato típico de la gastronomía peruana y ha tenido tanto éxito que sus sabores se han extendido a otros países latinoamericanos. Hay muchas versiones de ceviche y es imposible decir que una sola es la correcta, pero en esta oportunidad compartiremos contigo una receta de ceviche peruano que intenta rescatar todo lo tradicional del delicioso ceviche original del Perú. Muchos disfrutan el ceviche o cebiche solo, pero también puedes acompañarlo con ricas guarniciones. En este caso, hemos utilizado camote, maíz tierno y unas hojas de lechuga para refrescar.",
            ingredients = ingredients,
            steps = steps
        )

        firebaseRecipes.collection("recipes")
            .document(documentId)
            .set(recipe)


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
                db.groceryItemDao().addAll(*groceryItems.toTypedArray())
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
                    LocalDateTime.now().plusDays(5),
                    "Mangos",
                    3.0,
                    "kg",
                    LocalDateTime.now().plusDays(-2),
                    "https://cdn3.iconfinder.com/data/icons/fruits-52/150/icon_fruit_manga-256.png"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(7),
                    "Fresas",
                    200.0,
                    "g",
                    LocalDateTime.now().plusDays(-3),
                    "https://cdn3.iconfinder.com/data/icons/fruits-52/150/icon_fruit_morango-256.png"
                ),
                PantryItem(
                    0,
                    null,
                    LocalDateTime.now().plusDays(10),
                    "Limones",
                    6.0,
                    "",
                    LocalDateTime.now().plusDays(-4),
                    "https://cdn0.iconfinder.com/data/icons/fruity-3/512/Lemon-256.png"
                )
            )
            db.pantryItemDao().addAll(*pantryItems.toTypedArray())
        }
    }


}