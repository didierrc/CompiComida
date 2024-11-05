package com.example.compicomida.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.compicomida.R
import com.example.compicomida.db.LocalDatabase
import com.example.compicomida.db.entities.GroceryList
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

/**
 * AddGroceryListFragment:
 * - Shows a form to create a new grocery list.
 */
class AddGroceryListFragment : Fragment() {
    private lateinit var etListName: TextInputEditText
    private lateinit var btnAddList: Button
    private lateinit var db: LocalDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_grocery_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the view elements
        initialiseViewElements(view)

        // Init db
        db = LocalDatabase.getDB(requireContext())
        addOnClickListener(db)


    }


    private fun addOnClickListener(db: LocalDatabase) {
        btnAddList.setOnClickListener {
            val listName = etListName.text.toString().trim()
            if (listName.isBlank()) {
                val message = "El nombre de la lista no puede estar vacío"
                showAlert(message)
            } else {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (db.groceryListDao().getByName(listName) != null) {
                        showAlert("La lista ya existe")
                    } else {
                        db.groceryListDao()
                            .add(GroceryList(0, listName, LocalDateTime.now()))
                        etListName.text?.clear()
                        Log.e(
                            "AddGroceryListFragment",
                            "List added: $listName"
                        )
                        withContext(Dispatchers.Main) {
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }

    private fun showAlert(message: String, title: String = "Error") {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Ok", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun initialiseViewElements(view: View) {
        etListName = view.findViewById(R.id.et_list_name)
        btnAddList = view.findViewById(R.id.btn_add_list)
    }

}