package com.example.compicomida.views.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.viewmodels.grocery.ShoppingListsFragmentViewModel
import com.example.compicomida.viewmodels.grocery.factory.ShoppingListsFragmentViewModelFactory
import com.example.compicomida.views.activities.grocery.AddGroceryListActivity
import com.example.compicomida.views.activities.grocery.GroceryItemsListActivity
import com.example.compicomida.views.adapters.ShoppingListsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * ShoppingListsFragment:
 * - Shows a list with all the created grocery lists.
 */
class ShoppingListsFragment : Fragment() {

    private lateinit var recyclerGroceryList: RecyclerView
    private lateinit var addGroceryListLauncher: ActivityResultLauncher<Intent>

    private lateinit var viewModel: ShoppingListsFragmentViewModel
    private lateinit var shoppingListsAdapter: ShoppingListsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialise the Fab - Add new list.
        initFabNewList(view)

        // Initialise the view model
        viewModel = ViewModelProvider(
            this,
            ShoppingListsFragmentViewModelFactory(
                CompiComidaApp.appModule.groceryRepo
            )
        )[ShoppingListsFragmentViewModel::class.java]
        initialise()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshGroceryLists()
    }

    private fun initialise() {
        // Initialise the recycler views to empty lists.
        recyclerGroceryList = requireView().findViewById(R.id.recyclerGroceryList)
        recyclerGroceryList.layoutManager = LinearLayoutManager(requireContext())

        // Adapter for the recycler view.
        shoppingListsAdapter =
            ShoppingListsAdapter(
                HashMap(), { shopListId ->
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
                    viewModel.deleteGroceryList(groceryList)
                })

        // Set the adapter to the recycler view.
        recyclerGroceryList.adapter = shoppingListsAdapter

        // Observer for the grocery lists.
        viewModel.groceryLists.observe(viewLifecycleOwner) {
            shoppingListsAdapter.updateList(it)
        }
        // Fetching the data.
        viewModel.refreshGroceryLists()
    }

    // Initialise the Fab Click Listener
    private fun initFabNewList(view: View) {
        val fabNewList: FloatingActionButton = view.findViewById(R.id.fabNewlist)
        addGroceryListLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    viewModel.refreshGroceryLists()
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
}