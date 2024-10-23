package com.example.compicomida

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private var db: LocalDatabase? = null
    private val firestoreDB = FirebaseFirestore.getInstance()

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

        smallLocalDBTest()
        smallFirestoreDBTest()
    }

    // Put it into main to see if Firestore DB works OK
    // Probably this should be moved into TEST folder and Mocked.
    private fun smallFirestoreDBTest() {

        Log.d("Firestore", "******** Testing Firestore ********")

        firestoreDB.collection("meals")
            .get()
            .addOnSuccessListener { docs ->
                Log.d("Firestore", "****** Meals ******")
                for (doc in docs) {
                    Log.d("Firestore", "--> Meals: ${doc.id}")
                    Log.d("Firestore", "Name: ${doc.data["name"]}")
                    Log.d("Firestore", "Description: ${doc.data["description"]}")
                    Log.d("Firestore", "Difficulty: ${doc.data["difficulty"]}")
                    Log.d("Firestore", "Preparation time: ${doc.data["preparation_time"]}m")
                    Log.d("Firestore", "Image: ${doc.data["image"]}")
                    Log.d("Firestore", "Ingredients:")

                    val ingredients = doc.data["ingredients"] as? List<*>
                    ingredients?.forEach { ingredient ->
                        val i = ingredient as? Map<*, *>
                        Log.d("Firestore", "- ${i!!["name"]}: ${i["quantity"]} ${i["unit"]}")
                    }

                    // If the above works, the Steps part is easier :)
                    // Another thing is, could the above be improved instead of Casting?? TBD

                }
            }
    }

    // Put it into main to see if Local DB works OK
    // Probably this should be moved into TEST folder.
    private fun smallLocalDBTest() {
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