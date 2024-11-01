package com.example.compicomida.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.recyclerViews.ShoppingListsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ShoppingListsFragment:
 * - Shows a list with all the created grocery lists.
 */
class ShoppingListsFragment : Fragment() {

    private lateinit var recyclerGroceryList: RecyclerView
    private var db: LocalDatabase? = null

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
        fabNewList.setOnClickListener {
            findNavController().navigate(
                ShoppingListsFragmentDirections
                    .actionShoppingListsFragmentToAddGroceryListFragment()
            )
        }
    }

    // Shows all the current lists from DB.
    private fun initializeRecyclerGroceryList(db: LocalDatabase) {

        recyclerGroceryList = requireView().findViewById(R.id.recyclerGroceryList)
        recyclerGroceryList.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {

            val shoppingLists = db.groceryListDao().getAll()

            withContext(Dispatchers.Main) {
                recyclerGroceryList.adapter = ShoppingListsAdapter(shoppingLists) { shopListId ->
                    val target = shopListId?.let {
                        ShoppingListsFragmentDirections
                            .actionShoppingListsFragmentToGroceryItemsListFragment(it)
                    }


                    if (target != null)
                        findNavController().navigate(target)
                    else
                        Log.e("ShoppingListsFragment", "Target is null")
                }
            }
        }
    }
}