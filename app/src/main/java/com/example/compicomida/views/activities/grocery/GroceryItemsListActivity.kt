package com.example.compicomida.views.activities.grocery

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.databinding.ActivityGroceryItemsListBinding
import com.example.compicomida.viewmodels.grocery.GroceryItemsListViewModel
import com.example.compicomida.viewmodels.grocery.factory.GroceryItemsListViewModelFactory
import com.example.compicomida.views.adapters.GroceryItemsAdapter
import com.google.android.material.snackbar.Snackbar

class GroceryItemsListActivity : AppCompatActivity() {
    private lateinit var recyclerGroceryItem: RecyclerView
    private var listId: Int = 0
    private lateinit var binding: ActivityGroceryItemsListBinding
    private lateinit var addGroceryItemLauncher: ActivityResultLauncher<Intent>
    private lateinit var listGroceryItemsViewModel: GroceryItemsListViewModel

    companion object {
        const val ID_TAG = "GroceryItemsListActivity"
    }

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
        listGroceryItemsViewModel = ViewModelProvider(
            this,
            GroceryItemsListViewModelFactory(CompiComidaApp.appModule.groceryRepo, CompiComidaApp.appModule.pantryRepo, listId)
        )[GroceryItemsListViewModel::class.java]
        initialiseView()
    }

    private fun initialiseView() {
        initAddGroceryItemLauncher()
        initFabNewItem()
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        initializeRecyclerGroceryItems()
    }

    // Initialise the Fab Click Listener
    private fun initFabNewItem() {
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
                    listGroceryItemsViewModel.refreshGroceryItems()
                }
            }
    }

    override fun onResume() {
        super.onResume()
        listGroceryItemsViewModel.refreshGroceryItems()
    }

//    private fun confirmOnClickListener(){
//        binding.btFinishGroceryList.setOnClickListener(){
//            listGroceryItemsViewModel.addListToPantry(CompiComidaApp.appModule.pantryRepo)
//            CompiComidaApp.appModule.showAlert(
//                this,
//                getString(R.string.grocery_list_confirm_alert_text),
//                getString(R.string.grocery_list_confirm_alert_title)
//            )
//            listGroceryItemsViewModel.refreshGroceryItems()
//        }
//    }


    private fun initializeRecyclerGroceryItems() {
        // Initialise the recycler views to empty lists.
        recyclerGroceryItem = findViewById(R.id.recyclerGroceryItems)
        recyclerGroceryItem.layoutManager = GridLayoutManager(this, 2)

        // Adapter for the recycler view.
        val groceryItemListAdapter = GroceryItemsAdapter(
            listOf(),
            { itemId ->
                val intent = Intent(
                    this,
                    GroceryItemDetailsActivity::class.java
                )
                intent.putExtra(ID_TAG, itemId)
                startActivity(intent)
            },
            { groceryItem, checkState ->
                listGroceryItemsViewModel.checkItem(checkState, groceryItem!!.itemId)
                if(checkState){
                    Snackbar.make(
                        binding.root,
                        "Recuerda añadir la caducidad al producto en la despensa",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        )

        // Set the adapter to the recycler view.
        recyclerGroceryItem.adapter = groceryItemListAdapter

        // Observer for the grocery items
        listGroceryItemsViewModel.groceryItems.observe(this) {
            groceryItemListAdapter.updateData(it)
        }

        // Observer for the grocery list name
        listGroceryItemsViewModel.groceryListName.observe(this) {
            binding.toolbar.title = it
        }

        // Fetching the data.
        listGroceryItemsViewModel.refreshGroceryItems()
        listGroceryItemsViewModel.refreshGroceryListName()
    }
}