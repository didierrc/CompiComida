package com.example.compicomida.views.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.model.localDb.entities.GroceryList
import net.nicbell.materiallists.ListItem
import java.time.format.DateTimeFormatter

/**
 * Creates a view of a shopping list to be used
 * in a Recycler View.
 */
class ShoppingListsAdapter(

    private val shoppingLists: HashMap<GroceryList, Int>,
    private val onClickGoToItems: (Int?) -> Unit,
    private val onDeleteList: (GroceryList?) -> Unit,
) : RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = R.layout.recycler_grocery_list
        val view =
            LayoutInflater.from(viewGroup.context).inflate(layoutItem, viewGroup, false)
        return ViewHolder(view, onClickGoToItems, onDeleteList, ::numElementsOnList)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(shoppingLists.keys.sortedBy { it.createdAt }[position])
    }

    override fun getItemCount() = shoppingLists.size
    
    fun updateList(it: Map<GroceryList, Int>?) {
        if (it != null) {
            shoppingLists.clear()
            shoppingLists.putAll(it)
            notifyDataSetChanged()
        }
    }

    fun numElementsOnList(groceryList: GroceryList?): Int {
        return shoppingLists[groceryList] ?: 0
    }


    class ViewHolder(
        view: View,
        private val onClickGoToItems: (Int?) -> Unit,
        private val onDeleteList: (GroceryList?) -> Unit,
        private val numElementsOnList: (GroceryList?) -> Int
    ) : RecyclerView.ViewHolder(view) {

        private val listItem: ListItem =
            view.findViewById(R.id.recycler_grocery_list_item)
        private val btnDeleteList: AppCompatImageButton =
            view.findViewById(R.id.recycler_grocery_list_btn_delete)
        private val dateTimeFormatterDate =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        private val dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH:mm")

        private var groceryList: GroceryList? = null


        init {
            view.setOnClickListener {
                onClickGoToItems(groceryList?.listId)
            }
            btnDeleteList.setOnClickListener {
                btnDeleteList.isEnabled =
                    false // Disable the button to prevent multiple clicks
                btnDeleteList.setColorFilter(itemView.context.getColor(R.color.red))
                btnDeleteList.animate().alpha(0f).setDuration(300).withEndAction {
                    onDeleteList(groceryList)
                }.start()
            }
        }

        fun bind(groceryList: GroceryList) {
            this.groceryList = groceryList

            listItem.headline.text = groceryList.listName
            listItem.supportText.text = itemView.context.getString(
                R.string.recycler_grocery_list_date_and_products,
                groceryList.createdAt.format(dateTimeFormatterDate) + " a las " +
                        groceryList.createdAt.format(dateTimeFormatterTime),
                numElementsOnList(groceryList)
            )

            /**
             * This is a bug of our implementation probably
             * When we delete an item, the button is no longer visible
             * However, the ViewHolder may be reused for another list, leaving the
             * button invisible.
             */

            btnDeleteList.isEnabled = true
            btnDeleteList.alpha = 1f
            btnDeleteList.colorFilter = null

        }
    }

}