package com.example.compicomida.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.PantryItem
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
        pantryItemList = expireList
        notifyDataSetChanged()
    }

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {

        private val pantryElement = view.findViewById<ListItem>(R.id.expireItemElement)

        fun bind(pantryItem: PantryItem) {

            val unit = pantryItem.unit ?: ""
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val expireDate = pantryItem.expirationDate.format(formatter)

            val expireOn = if (pantryItem.expirationDate.isBefore(LocalDateTime.now()))
                "Vence HOY"
            else if (pantryItem.expirationDate.isBefore(LocalDateTime.now().plusDays(1)))
                "Vence MAÑANA"
            else
                "Vence el $expireDate"

            pantryElement.headline.text =
                "${pantryItem.pantryName} • ${pantryItem.quantity} $unit"
            pantryElement.supportText.text = expireOn
        }
    }
}