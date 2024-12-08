package com.example.compicomida.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.PantryItem
import com.example.compicomida.views.adapters.diff.PantryDiffCallback
import net.nicbell.materiallists.ListItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ExpireItemsAdapter(
    private var pantryItemList: List<PantryItem>,
) : RecyclerView.Adapter<ExpireItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = R.layout.recycler_expire_list
        val view =
            LayoutInflater.from(viewGroup.context).inflate(layoutItem, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) =
        viewHolder.bind(pantryItemList[position])

    override fun getItemCount() = pantryItemList.size

    fun updateList(expireList: List<PantryItem>) {
        val diffCallback = PantryDiffCallback(pantryItemList, expireList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        pantryItemList = expireList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val pantryElement = view.findViewById<ListItem>(R.id.expireItemElement)

        fun bind(pantryItem: PantryItem) {

            val unit = pantryItem.unit ?: ""
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val expireDate = pantryItem.expirationDate.format(formatter)

            val expireOn =
                when (pantryItem.expirationDate.dayOfMonth) {
                    LocalDateTime.now().dayOfMonth -> "Vence HOY"
                    LocalDateTime.now()
                        .plusDays(1).dayOfMonth -> "Vence MAÑANA"

                    LocalDateTime.now()
                        .plusDays(2).dayOfMonth -> "Vence PASADO MAÑANA"

                    else -> "Vence el $expireDate" // Shouldn't happen
                }

            pantryElement.headline.text = itemView.context.getString(
                R.string.expire_items_headline_text,
                pantryItem.pantryName,
                CompiComidaApp.appModule.parseQuantity(pantryItem.quantity),
                unit
            )
            pantryElement.supportText.text = expireOn
        }
    }
}