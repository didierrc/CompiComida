package com.example.compicomida.views.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.example.compicomida.model.localDb.entities.PantryItem

class PantryDiffCallback(
    private val oldList: List<PantryItem>,
    private val newList: List<PantryItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].pantryId == newList[newItemPosition].pantryId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]

        return oldItem.itemId == newItem.itemId &&
                oldItem.expirationDate == newItem.expirationDate &&
                oldItem.pantryName == newItem.pantryName &&
                oldItem.quantity == newItem.quantity &&
                oldItem.unit == newItem.unit &&
                oldItem.lastUpdate == newItem.lastUpdate &&
                oldItem.pantryPhotoUri == newItem.pantryPhotoUri
    }
}