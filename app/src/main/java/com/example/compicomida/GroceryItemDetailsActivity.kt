package com.example.compicomida

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.example.compicomida.databinding.ActivityGroceryItemDetailsBinding
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.ItemCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroceryItemDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroceryItemDetailsBinding
    private var groceryItemID: Int = 0
    private lateinit var groceryItem: GroceryItem
    private lateinit var db: LocalDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroceryItemDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root) // Use binding.root here
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.grocery_item_details)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        groceryItemID = intent.getIntExtra(GroceryItemsListActivity.ID_TAG, 0)
        db = LocalDatabase.getDB(applicationContext)

        configureBehaviour()


    }

    private fun configureBehaviour() {
        lifecycleScope.launch(Dispatchers.IO) {
            groceryItem = db.groceryItemDao().getById(groceryItemID)!!
            val groceryItemCategory =
                db.itemCategoryDao().getById(groceryItem.categoryId!!)
            withContext(Dispatchers.Main) {
                setUpActionBar()
                fillItemDetails(groceryItemCategory)
                binding.itemImage.load(groceryItem.itemPhotoUri)
            }

        }

        with(binding) {
            itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
                checkGroceryItem(isChecked)
            }
            btnDelete.setOnClickListener() {
                lifecycleScope.launch(Dispatchers.IO) {
                    db.groceryItemDao().delete(groceryItem)
                    finish()
                }
            }
        }


    }

    private fun setUpActionBar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        toolbar.title = groceryItem.itemName + " - Detalles"

    }

    private fun fillItemDetails(groceryItemCategory: ItemCategory?) {
        binding.itemName.text = groceryItem.itemName
        binding.itemCategory.text = groceryItemCategory?.categoryName ?: "Sin categoría"
        binding.itemQuantity.text = getString(
            R.string.quantity_unit,
            groceryItem.quantity.toString(),
            groceryItem.unit ?: "Unidades"
        )
        binding.itemPrice.text = if (groceryItem.unit.isNullOrEmpty()) {
            getString(
                R.string.price_no_unit,
                groceryItem.price
            )
        } else {
            getString(
                R.string.price_per_unit,
                groceryItem.price,
                groceryItem.unit
            )
        }
        binding.itemCheckbox.isChecked = groceryItem.isPurchased
    }

    private fun checkGroceryItem(
        checkState: Boolean,
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            groceryItem.isPurchased = checkState
            db.groceryItemDao().update(groceryItem)
        }

    }
}