package com.example.compicomida.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.GroceryList
import com.example.compicomida.views.activities.grocery.AddGroceryListActivity
import com.example.compicomida.views.activities.grocery.GroceryItemsListActivity
import com.example.compicomida.views.adapters.ShoppingListsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * ShoppingListsFragment:
 * - Shows a list with all the created grocery lists.
 */
class ShoppingListsFragment : Fragment() {

    private lateinit var recyclerGroceryList: RecyclerView
    private var db: LocalDatabase? = null
    private lateinit var addGroceryListLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Shows all the current lists from DB.
        db = LocalDatabase.getDB(requireContext())
        db?.let { initializeRecyclerGroceryList(it) }

        // Initialise the Fab - Add new list.
        initFabNewList(view)
    }

    // Initialise the Fab Click Listener
    private fun initFabNewList(view: View) {
        val fabNewList: FloatingActionButton = view.findViewById(R.id.fabNewlist)
        addGroceryListLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    db?.let { initializeRecyclerGroceryList(it) }
                }
            }
        fabNewList.setOnClickListener {
            addGroceryListLauncher.launch(
                Intent(
                    requireContext(),
                    AddGroceryListActivity::class.java
                )
            )
        }
    }

    // Shows all the current lists from DB.
    private fun initializeRecyclerGroceryList(db: LocalDatabase) {

        recyclerGroceryList = requireView().findViewById(R.id.recyclerGroceryList)
        recyclerGroceryList.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {

            val shoppingLists = db.groceryListDao.getAll()

            withContext(Dispatchers.Main) {
                recyclerGroceryList.adapter =
                    ShoppingListsAdapter(shoppingLists, { shopListId ->
                        shopListId?.let {
                            val intent =
                                Intent(
                                    requireContext(),
                                    GroceryItemsListActivity::class.java
                                )
                            intent.putExtra("listId", it)
                            startActivity(intent)
                        }
                    },
                        { groceryList ->
                            deleteGroceryList(groceryList, db)
                        }, { groceryList ->
                            getNumberOfItemsOnList(groceryList, db)
                        })

            }
        }
    }

    private fun getNumberOfItemsOnList(
        groceryList: GroceryList?,
        db: LocalDatabase
    ): Int {
        return if (groceryList != null) {
            runBlocking {
                db.groceryListDao.getListSize(groceryList.listId)
            }
        } else {
            Log.e("ShoppingListsFragment", "GroceryList is null")
            0
        }
    }


    private fun deleteGroceryList(groceryList: GroceryList?, db: LocalDatabase) {
        if (groceryList != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                db.groceryListDao.delete(groceryList)
                withContext(Dispatchers.Main) {
                    initializeRecyclerGroceryList(db)
                }
            }
        } else {
            Log.e("ShoppingListsFragment", "GroceryList is null")
        }
    }
}