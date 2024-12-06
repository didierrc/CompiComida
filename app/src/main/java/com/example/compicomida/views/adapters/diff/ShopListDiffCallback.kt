package com.example.compicomida.views.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.example.compicomida.model.localDb.entities.GroceryList

class ShopListDiffCallback(
    private val oldShopList: Map<GroceryList, Int>,
    private val newShopList: Map<GroceryList, Int>,
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldShopList.size
    override fun getNewListSize(): Int = newShopList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldShopList.keys.elementAt(oldItemPosition).listId == newShopList.keys.elementAt(
            newItemPosition
        ).listId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

        val oldList = oldShopList.keys.elementAt(oldItemPosition)
        val newList = newShopList.keys.elementAt(newItemPosition)

        return oldList.listName == newList.listName &&
                oldList.createdAt == newList.createdAt &&
                oldShopList[oldList] == newShopList[newList]
    }


}