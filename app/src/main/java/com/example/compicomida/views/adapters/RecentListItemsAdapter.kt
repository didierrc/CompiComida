package com.example.compicomida.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.views.adapters.diff.GroceryDiffCallback
import net.nicbell.materiallists.ListItem

class RecentListItemsAdapter(
    private var groceryItemList: List<GroceryItem>,
) : RecyclerView.Adapter<RecentListItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = R.layout.recycler_recent_items_list
        val view =
            LayoutInflater.from(viewGroup.context).inflate(layoutItem, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) =
        viewHolder.bind(groceryItemList[position])


    override fun getItemCount() = groceryItemList.size

    fun updateList(recentList: List<GroceryItem>) {
        val diffCallback = GroceryDiffCallback(groceryItemList, recentList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        groceryItemList = recentList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val groceryElement = view.findViewById<ListItem>(R.id.recentItemElement)

        fun bind(groceryItem: GroceryItem) {

            with(groceryItem) {
                val quantityParse = if (quantity.mod(1.0) == 0.0) {
                    quantity.toInt().toString()
                } else {
                    if (quantity == 0.5)
                        "1/2"
                    else
                        quantity.toString()
                }

                groceryElement.headline.text =
                    "${groceryItem.itemName} • $quantityParse ${groceryItem.unit ?: ""}"
            }


        }
    }
}