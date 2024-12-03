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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.viewmodels.factories.PantryViewModelFactory
import com.example.compicomida.viewmodels.pantry.PantryViewModel
import com.example.compicomida.views.activities.pantry.AddPantryItemActivity
import com.example.compicomida.views.activities.pantry.EditPantryItemActivity
import com.example.compicomida.views.adapters.PantryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * PantryFragment:
 * - Shows a list with all the products from your pantry.
 */
class PantryFragment : Fragment() {

    // View Model
    private lateinit var pantryModel: PantryViewModel

    // Recycler Lists
    private lateinit var recyclerPantry: RecyclerView

    // Adapter Lists
    private lateinit var pantryAdapter: PantryAdapter

    // Activity launcher
    private lateinit var activityLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_pantry, container, false)

        // Creating the Recycler Views
        recyclerPantry = root.findViewById(R.id.recyclerPantry)
        recyclerPantry.layoutManager = GridLayoutManager(root.context, 2)

        // Initialise the view model
        pantryModel = ViewModelProvider(
            requireActivity(),
            PantryViewModelFactory(CompiComidaApp.appModule.pantryRepo)
        )[PantryViewModel::class.java]


        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) pantryModel.refreshPantryList()
            }

        initialisePantryRecyclerView()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFabNewPantryItem(view)
    }

    // Initialise the Fab Click Listener
    private fun initFabNewPantryItem(view: View) {
        val fabNewItem = view.findViewById<FloatingActionButton>(R.id.fabNewItemInPantry)
        fabNewItem.setOnClickListener {
            activityLauncher.launch(
                Intent(
                    context,
                    AddPantryItemActivity::class.java
                )
            )
        }
    }

    // Initialise the recycler view
    private fun initialisePantryRecyclerView() {
        pantryAdapter = PantryAdapter(listOf()) { pantryItemId ->
            pantryItemId?.let {

                activityLauncher.launch(
                    Intent(
                        context,
                        EditPantryItemActivity::class.java
                    ).apply {
                        putExtra("pantryId", it)
                    }
                )
            }
        }
        recyclerPantry.adapter = pantryAdapter

        // Observe pantry data
        pantryModel.pantryList.observe(viewLifecycleOwner) { pantryList ->
            pantryAdapter.updateList(pantryList)
        }

        pantryModel.refreshPantryList()
    }

}