package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.db.entities.GroceryItem

class GroceryItemsAdapter(

    private var groceryItems: List<GroceryItem>,
    private val onClickGoToItemDetail: (Int?) -> Unit,
    private val onDeleteItem: (GroceryItem?) -> Unit,
    private val onCheckItem: (GroceryItem?, Boolean) -> Unit

) : RecyclerView.Adapter<GroceryItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemLayout = R.layout.recycler_grocery_item
        val view =
            LayoutInflater.from(viewGroup.context).inflate(itemLayout, viewGroup, false)
        return ViewHolder(view, onClickGoToItemDetail, onDeleteItem, onCheckItem)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(groceryItems[position])
    }

    override fun getItemCount() = groceryItems.size


    class ViewHolder(
        view: View,
        onClickGoToItemDetail: (Int?) -> Unit,
        onDeleteItem: (GroceryItem?) -> Unit,
        onCheckItem: (GroceryItem?, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView =
            view.findViewById(R.id.recycler_grocery_item_title)
        private val tvText: TextView = view.findViewById(R.id.recycler_grocery_item_text)
        private val cbPurchased: CheckBox =
            view.findViewById(R.id.recycler_grocery_item_checkBox)
        private val imageView: ImageView =
            view.findViewById(R.id.recycler_grocery_item_image)
        private val btnDeleteItem: Button =
            view.findViewById(R.id.recycler_grocery_item_btn_delete)

        private var groceryItem: GroceryItem? = null

        init {
            view.setOnClickListener { onClickGoToItemDetail(groceryItem?.itemId) }
            with(btnDeleteItem) {
                setOnClickListener {
                    isEnabled = false // Disable the button to prevent multiple clicks
                    animate().alpha(0f).setDuration(400).withEndAction {
                        onDeleteItem(groceryItem)
                    }.start()
                }
            }
            cbPurchased.setOnCheckedChangeListener { _, isChecked ->
                onCheckItem(groceryItem, isChecked)
            }
        }

        fun bind(groceryItem: GroceryItem) {
            this.groceryItem = groceryItem

            tvTitle.text = groceryItem.itemName

            var unit = ""
            if (groceryItem.unit != "No especificada") {
                unit = groceryItem.unit.toString()
            }
            tvText.text = itemView.context.getString(
                R.string.grocery_items_adapter_cantidad_text,
                groceryItem.quantity,
                unit
            )
            cbPurchased.isChecked = groceryItem.isPurchased
            imageView.load(groceryItem.itemPhotoUri)
        }
    }
}