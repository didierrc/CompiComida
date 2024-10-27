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
import com.example.compicomida.db.converters.DateConverter
import com.example.compicomida.db.entities.GroceryList
import com.example.compicomida.recyclerViews.ShoppingListsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime


class ShoppingListsFragment : Fragment() {
    private lateinit var recyclerGroceryList : RecyclerView
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val dateConverter = DateConverter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_lists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecylerGroceryList()


        val fabNewList = view.findViewById<FloatingActionButton>(R.id.fabNewlist)
        fabNewList.setOnClickListener {
            val destino = ShoppingListsFragmentDirections
                .actionShoppingListsFragmentToAddGroceryListFragment()
            findNavController().navigate(destino)
        }
    }

    private fun initializeRecylerGroceryList() {
        recyclerGroceryList = requireView().findViewById(R.id.recyclerGroceryList)
        //Fíjate cómo le pasamos el contexto. Ya no es "this" ¡Estamos en un fragment!
        recyclerGroceryList.layoutManager = LinearLayoutManager(requireContext())


        lifecycleScope.launch(Dispatchers.IO) {
            var shoppingLists : List<GroceryList> = List(5) { i -> GroceryList(i,"Lista $i", LocalDateTime.now())}


            /*var shoppingLists : MutableList<GroceryList> = mutableListOf()

            firestoreDB.collection("groceryLists")
                .get()
                .addOnSuccessListener { groceryLists ->
                    Log.d("Firestore", "****** Grocery Lists ******")
                    for (groceryList in groceryLists) {
                        Log.d("Firestore", "--> List: ${groceryList.id}")
                        Log.d("Firestore", "Name: ${groceryList.data["listName"]}")
                        Log.d("Firestore", "Created at: ${groceryList.data["createdAt"]}")

                        shoppingLists.add( GroceryList(groceryList.id.toInt(),groceryList.data["listName"].toString(),dateConverter.fromTimestamp(groceryList.data["createdAt"].toString())!!))

                    }
                }*/

            withContext(Dispatchers.Main) {
                recyclerGroceryList.adapter = ShoppingListsAdapter(shoppingLists) { shoppingList ->
                    val destino = ShoppingListsFragmentDirections.actionShoppingListsFragmentToGroceryItemsListFragment(shoppingList!!.listId)
                    findNavController().navigate(destino)
                }
            }
        }
    }
}