package com.example.compicomida.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.views.adapters.diff.GroceryDiffCallback

class GroceryItemsAdapter(

    private var groceryItems: List<GroceryItem>,
    private val onClickGoToItemDetail: (Int?) -> Unit,
    private val onCheckItem: (GroceryItem?, Boolean) -> Unit

) : RecyclerView.Adapter<GroceryItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val itemLayout = R.layout.recycler_grocery_item
        val view =
            LayoutInflater.from(viewGroup.context).inflate(itemLayout, viewGroup, false)
        return ViewHolder(view, onClickGoToItemDetail, onCheckItem)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(groceryItems[position])
    }

    override fun getItemCount() = groceryItems.size

    fun updateData(newGroceryItems: List<GroceryItem>) {
        val diffCallback = GroceryDiffCallback(groceryItems, newGroceryItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        groceryItems = newGroceryItems
        diffResult.dispatchUpdatesTo(this)
    }


    class ViewHolder(
        view: View,
        onClickGoToItemDetail: (Int?) -> Unit,
        onCheckItem: (GroceryItem?, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val tvTitle: TextView =
            view.findViewById(R.id.recycler_grocery_item_title)
        private val tvText: TextView = view.findViewById(R.id.recycler_grocery_item_text)
        private val cbPurchased: CheckBox =
            view.findViewById(R.id.recycler_grocery_item_checkBox)
        private val imageView: ImageView =
            view.findViewById(R.id.recycler_grocery_item_image)

        private var groceryItem: GroceryItem? = null

        init {
            view.setOnClickListener {
                onClickGoToItemDetail(groceryItem?.itemId)
            }
            cbPurchased.setOnClickListener {
                onCheckItem(groceryItem, cbPurchased.isChecked)
            }
        }

        fun bind(groceryItem: GroceryItem) {
            this.groceryItem = groceryItem

            tvTitle.text = groceryItem.itemName
            tvText.text =
                CompiComidaApp.appModule.parseUnitQuantity(groceryItem.unit, groceryItem.quantity)
            cbPurchased.isChecked = groceryItem.isPurchased
            imageView.load(groceryItem.itemPhotoUri)
        }


    }
}