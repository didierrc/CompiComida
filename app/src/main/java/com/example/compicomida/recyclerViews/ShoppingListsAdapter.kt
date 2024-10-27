package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.db.converters.DateConverter
import com.example.compicomida.db.entities.GroceryList

class ShoppingListsAdapter(

    private val shoppingLists: List<GroceryList>,
    private val onClickListener: (GroceryList?) -> Unit

) : RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutElemento = R.layout.recycler_grocery_list
        val view = LayoutInflater.from(viewGroup.context).inflate(layoutElemento, viewGroup, false)
        return ViewHolder(view, onClickListener)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(shoppingLists[position])
    }

    override fun getItemCount() = shoppingLists.size


    class ViewHolder(
        view: View,
        onClickListener: (GroceryList?) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val tvName: TextView = view.findViewById(R.id.recycler_grocery_list_name)
        private val tvDate: TextView = view.findViewById(R.id.recycler_grocery_list_date)
        private val imageView: ImageView = view.findViewById(R.id.recycler_grocery_list_image)
        private var actualGroceryList: GroceryList? = null

        init {
            view.setOnClickListener { it ->
                onClickListener(actualGroceryList)
            }
        }
        fun bind(groceryList : GroceryList) {
            actualGroceryList = groceryList
            tvName.text = groceryList.listName
            tvDate.text = groceryList.createdAt.toString()
            //to let users customize the image of the lists
            //imageView.load(groceryList.imagenURL)
        }
    }

}