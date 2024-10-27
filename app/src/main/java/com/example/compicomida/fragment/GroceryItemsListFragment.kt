package com.example.compicomida.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.db.converters.DateConverter
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList
import com.example.compicomida.recyclerViews.GroceryItemsAdapter
import com.example.compicomida.recyclerViews.ShoppingListsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class GroceryItemsListFragment : Fragment() {

    private lateinit var recyclerGroceryItem : RecyclerView
    private val firestoreDB = FirebaseFirestore.getInstance()

    private val args : GroceryItemsListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_grocery_items_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecylerGroceryItem()


        val fabNewItem = view.findViewById<FloatingActionButton>(R.id.fabNewItem)
        fabNewItem.setOnClickListener {
            val destino = GroceryItemsListFragmentDirections
                .actionGroceryItemsListFragmentToAddGroceryItemFragment()
            findNavController().navigate(destino)
        }
    }

    private fun initializeRecylerGroceryItem() {
        recyclerGroceryItem = requireView().findViewById(R.id.recyclerGroceryItem)
        //Fíjate cómo le pasamos el contexto. Ya no es "this" ¡Estamos en un fragment!
        recyclerGroceryItem.layoutManager = LinearLayoutManager(requireContext())


        lifecycleScope.launch(Dispatchers.IO) {
            var groceryItems : List<GroceryItem> = List(5) { i -> GroceryItem(i,args.listId, 0, "Producto ${i}", (i * 2 + 1),null,(i + 0.1),if (i < 2) false else true,null)}


            /*var groceryItems : MutableList<GroceryItem> = mutableListOf()

            firestoreDB.collection("groceryItems")
                .get()
                .addOnSuccessListener { groceryItems ->
                    Log.d("Firestore", "****** Grocery Items ******")
                    for (groceryItem in groceryItems) {

                        groceryItems.add( GroceryItem(i,args.listId, 0, "Producto ${i}",(i * 2 + 1) ,null,(i + 0.1),false,null) )
                    }
                }*/
            withContext(Dispatchers.Main) {
                recyclerGroceryItem.adapter = GroceryItemsAdapter(groceryItems) { //will change if the product is purchare or not
                }
            }

        }
    }
}