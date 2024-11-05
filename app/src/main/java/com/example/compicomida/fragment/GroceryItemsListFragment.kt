package com.example.compicomida.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.recyclerViews.GroceryItemsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * GroceryItemsListFragment:
 * - Shows a list with all the products from a grocery list.
 */
class GroceryItemsListFragment : Fragment() {

    private lateinit var recyclerGroceryItem: RecyclerView
    private val args: GroceryItemsListFragmentArgs by navArgs()
    private var db: LocalDatabase? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grocery_items_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        // Shows all the current items inside a shopping list from DB.
        db = LocalDatabase.getDB(requireContext())
        db?.let { initializeRecyclerItemsList(it) }

        initFabNewList(view)
    }

    // Initialise the Fab Click Listener
    private fun initFabNewList(view: View) {
        val fabNewItem = view.findViewById<FloatingActionButton>(R.id.fabNewItem)
        fabNewItem.setOnClickListener {
            val target = GroceryItemsListFragmentDirections
                .actionGroceryItemsListFragmentToAddGroceryItemFragment(args.listId)
            findNavController().navigate(target)
        }
    }

    private fun initializeRecyclerItemsList(db: LocalDatabase) {
        recyclerGroceryItem = requireView().findViewById(R.id.recyclerGroceryItem)
        recyclerGroceryItem.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {
            val groceryItems = db.groceryItemDao().getByListId(args.listId)

            withContext(Dispatchers.Main) {
                val adapter = GroceryItemsAdapter(groceryItems, { itemId ->
                    Toast.makeText(
                        requireContext(),
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