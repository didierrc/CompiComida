package com.example.compicomida.views.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.example.compicomida.model.localDb.entities.GroceryItem

class GroceryDiffCallback(
    private val oldGroceryList: List<GroceryItem>,
    private val newGroceryList: List<GroceryItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldGroceryList.size
    override fun getNewListSize(): Int = newGroceryList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldGroceryList[oldItemPosition].itemId == newGroceryList[newItemPosition].itemId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldGroceryList[oldItemPosition]
        val newItem = newGroceryList[newItemPosition]

        return oldItem.listId == newItem.listId &&
                oldItem.categoryId == newItem.categoryId &&
                oldItem.itemName == newItem.itemName &&
                oldItem.quantity == newItem.quantity &&
                oldItem.unit == newItem.unit &&
                oldItem.price == newItem.price &&
                oldItem.isPurchased == newItem.isPurchased &&
                oldItem.itemPhotoUri == newItem.itemPhotoUri
    }


}