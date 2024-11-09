package com.example.compicomida

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.databinding.ActivityGroceryItemsListBinding
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.recyclerViews.GroceryItemsAdapter
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroceryItemsListActivity : AppCompatActivity() {
    private lateinit var db: LocalDatabase
    private lateinit var recyclerGroceryItem: RecyclerView
    private var listId: Int = 0
    private lateinit var binding: ActivityGroceryItemsListBinding
    private lateinit var groceryItems: List<GroceryItem>
    private lateinit var addGroceryItemLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGroceryItemsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.groceryItemsList)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        listId = intent.getIntExtra("listId", 0)
        // Shows all the current items inside a shopping list from DB.
        db = LocalDatabase.getDB(this)
        db.let { initializeRecyclerItemsList(it) }

        initAddGroceryItemLauncher()
        initFabNewList()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    // Initialise the Fab Click Listener
    private fun initFabNewList() {
        val fabNewItem = binding.fabNewItem

        fabNewItem.setOnClickListener {
            addGroceryItemLauncher.launch(
                Intent(
                    this,
                    AddGroceryItemActivity::class.java
                ).apply {
                    putExtra("listId", listId)
                })

        }
    }

    private fun initAddGroceryItemLauncher() {
        addGroceryItemLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    initializeRecyclerItemsList(db)
                }
            }
    }

    private fun initializeRecyclerItemsList(db: LocalDatabase) {
        recyclerGroceryItem = findViewById(R.id.recyclerGroceryItems)
        recyclerGroceryItem.layoutManager = GridLayoutManager(this, 2)

        lifecycleScope.launch(Dispatchers.IO) {
            groceryItems = db.groceryItemDao().getByListId(listId)
            val listName = db.groceryListDao().getById(listId)?.listName
            val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
            toolbar.title = listName
            withContext(Dispatchers.Main) {
                val adapter = GroceryItemsAdapter(groceryItems, { itemId ->
                    Toast.makeText(
                        this@GroceryItemsListActivity,
                        "Item clicked: $itemId",
                        Toast.LENGTH_SHORT
                    ).show()
                }, { groceryItem ->
                    deleteGroceryItem(groceryItem, db)
                },
                    { groceryItem, checkState ->
                        checkGroceryItem(groceryItem, checkState, db)
                    })

                recyclerGroceryItem.adapter = adapter
            }

        }
    }

    private fun checkGroceryItem(
        groceryItem: GroceryItem?,
        checkState: Boolean,
        db: LocalDatabase
    ) {
        if (groceryItem != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                groceryItem.isPurchased = checkState
                db.groceryItemDao().update(groceryItem)
            }
        }

    }

    private fun deleteGroceryItem(groceryItem: GroceryItem?, db: LocalDatabase) {
        if (groceryItem != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                db.groceryItemDao().delete(groceryItem)
                withContext(Dispatchers.Main) {
                    initializeRecyclerItemsList(db)
                }
            }
        }
    }


}