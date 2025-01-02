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
import java.time.format.DateTimeFormatter

class AlreadyExpiredItemsAdapter(
    private var pantryItemList: List<PantryItem>,
    private val onDeletePantryItem: (PantryItem?) -> Unit
) : RecyclerView.Adapter<AlreadyExpiredItemsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = R.layout.recycler_already_expire_list
        val view =
            LayoutInflater.from(viewGroup.context).inflate(layoutItem, viewGroup, false)
        return ViewHolder(view, onDeletePantryItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(pantryItemList[position])

    override fun getItemCount(): Int = pantryItemList.size

    fun updateList(expireList: List<PantryItem>) {
        val diffCallback = PantryDiffCallback(pantryItemList, expireList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        pantryItemList = expireList
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(
        private val view: View,
        private val onDeletePantryItem: (PantryItem?) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val deleteIcon = view.findViewById<View>(R.id.alreadyExpireItemTrailingIcon)
        private val pantryElement = view.findViewById<ListItem>(R.id.alreadyExpireItemElement)
        private var pantryItem: PantryItem? = null

        init {
            deleteIcon.setOnClickListener {
                deleteIcon.isEnabled =
                    false // Disable the button to prevent multiple clicks
                deleteIcon.animate().alpha(0f).setDuration(300).withEndAction {
                    onDeletePantryItem(pantryItem)
                }.start()
            }
        }

        fun bind(pantryItem: PantryItem) {

            this.pantryItem = pantryItem

            val unit = pantryItem.unit ?: ""
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val expireOn = view.context.getString(
                R.string.expire_on,
                pantryItem.expirationDate.format(formatter)
            )

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