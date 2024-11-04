package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.db.entities.GroceryItem

class GroceryItemsAdapter(

    private val groceryItems: List<GroceryItem>,
    private val onClickGoToItemDetail: (Int?) -> Unit

) : RecyclerView.Adapter<GroceryItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemLayout = R.layout.recycler_grocery_item
        val view =
            LayoutInflater.from(viewGroup.context).inflate(itemLayout, viewGroup, false)
        return ViewHolder(view, onClickGoToItemDetail)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(groceryItems[position])
    }

    override fun getItemCount() = groceryItems.size


    class ViewHolder(
        view: View,
        onClickGoToItemDetail: (Int?) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView =
            view.findViewById(R.id.recycler_grocery_item_title)
        private val tvText: TextView = view.findViewById(R.id.recycler_grocery_item_text)
        private val cbPurchared: CheckBox =
            view.findViewById(R.id.recycler_grocery_item_checkBox)
        private val imageView: ImageView =
            view.findViewById(R.id.recycler_grocery_item_image)

        private var groceryItem: GroceryItem? = null

        init {
            view.setOnClickListener { onClickGoToItemDetail(groceryItem?.itemId) }
        }

        fun bind(groceryItem: GroceryItem) {
            this.groceryItem = groceryItem

            tvTitle.text = groceryItem.itemName
            tvText.text = itemView.context.getString(
                R.string.grocery_items_adapter_cantidad_text,
                groceryItem.quantity,
                groceryItem.unit ?: ""
            )
            cbPurchared.isChecked = groceryItem.isPurchased
            imageView.load(groceryItem.itemPhotoUri)
        }
    }

}