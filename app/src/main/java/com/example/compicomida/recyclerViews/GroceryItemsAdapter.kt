package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.db.converters.DateConverter
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList

class GroceryItemsAdapter(

    private val groceryItems: List<GroceryItem>,
    private val onClickListener: (GroceryItem?) -> Unit

) : RecyclerView.Adapter<GroceryItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutElemento = R.layout.recycler_grocery_item
        val view = LayoutInflater.from(viewGroup.context).inflate(layoutElemento, viewGroup, false)
        return ViewHolder(view, onClickListener)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(groceryItems[position])
    }

    override fun getItemCount() = groceryItems.size


    class ViewHolder(
        view: View,
        onClickListener: (GroceryItem?) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView = view.findViewById(R.id.recycler_grocery_item_title)
        private val tvText: TextView = view.findViewById(R.id.recycler_grocery_item_text)
        private val cbPurchared : CheckBox = view.findViewById(R.id.recycler_grocery_item_checkBox)
        private val imageView: ImageView = view.findViewById(R.id.recycler_grocery_item_image)
        private var actualGroceryItem: GroceryItem? = null

        init {
            view.setOnClickListener { it ->
                onClickListener(actualGroceryItem)
            }
        }
        fun bind(groceryItem : GroceryItem) {
            actualGroceryItem = groceryItem
            tvTitle.text = groceryItem.itemName
            tvText.text = "Cantidad: ${groceryItem.quantity}"
            cbPurchared.isChecked  = groceryItem.isPurchased
            //to let users customize the image of the lists
            //imageView.load(groceryItem.itemPhotoUri)
        }
    }

}