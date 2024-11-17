package com.example.compicomida.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.AddPantryItemActivity
import com.example.compicomida.R
import com.example.compicomida.databinding.FragmentPantryBinding
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.PantryItem
import com.example.compicomida.recyclerViews.PantryAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * PantryFragment:
 * - Shows a list with all the products from your pantry.
 */
class PantryFragment : Fragment() {
    private lateinit var db: LocalDatabase
    private lateinit var recyclerPantry: RecyclerView
    private lateinit var addPantryItemLauncher: ActivityResultLauncher<Intent>
    private lateinit var editPantryItemLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pantry, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Shows all the current lists from DB.
        db = LocalDatabase.getDB(requireContext())
        db?.let { initializeRecyclerPantry(it) }

        initAddPantryItemLauncher()
        initEditPantryItemLauncher()
        initFabNewItem(view)
    }

    // Initialise the Fab Click Listener
    private fun initFabNewItem(view: View) {
        val fabNewItem: FloatingActionButton = view.findViewById(R.id.fabNewItemInPantry)
        fabNewItem.setOnClickListener {
            addPantryItemLauncher.launch(
                Intent(
                    requireView().context,
                    AddPantryItemActivity::class.java
                )
            )
        }
    }

    private fun initAddPantryItemLauncher() {
        addPantryItemLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    initializeRecyclerPantry(db)
                }
            }
    }

    private fun initEditPantryItemLauncher() {
        editPantryItemLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    initializeRecyclerPantry(db)
                }
            }
    }

    private fun initializeRecyclerPantry(db: LocalDatabase) {
        recyclerPantry = requireView().findViewById(R.id.recyclerPantry)
        recyclerPantry.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {

            val pantryList = db.pantryItemDao().getAll()

            withContext(Dispatchers.Main) {
                recyclerPantry.adapter = PantryAdapter(pantryList
                ) { pantryItemId ->
                    pantryItemId?.let {
                        editPantryItemLauncher.launch(
                            Intent(
                                requireView().context,
                                AddPantryItemActivity::class.java
                            ).apply {
                                putExtra("pantryId", it)
                            }
                        )
                    }
                }
            }
        }
    }

    private fun deletePantryItem(pantryItem: PantryItem?, db: LocalDatabase) {
        if (pantryItem != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                db.pantryItemDao().delete(pantryItem)
                withContext(Dispatchers.Main) {
                    initializeRecyclerPantry(db)
                }
            }
        } else {
            Log.e("PantryFragment", "PantryItem is null")
        }
    }

}