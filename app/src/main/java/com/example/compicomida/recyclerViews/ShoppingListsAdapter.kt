package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import com.example.compicomida.R
import com.example.compicomida.db.entities.GroceryList
import net.nicbell.materiallists.ListItem
import java.time.format.DateTimeFormatter

/**
 * Creates a view of a shopping list to be used
 * in a Recycler View.
 */
class ShoppingListsAdapter(

    private val shoppingLists: List<GroceryList>,
    private val onClickGoToItems: (Int?) -> Unit,
    private val onDeleteList: (GroceryList?) -> Unit,
    private val numElementsOnList: (GroceryList?) -> Int

) : RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = R.layout.recycler_grocery_list
        val view =
            LayoutInflater.from(viewGroup.context).inflate(layoutItem, viewGroup, false)
        return ViewHolder(view, onClickGoToItems, onDeleteList, numElementsOnList)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(shoppingLists[position])
    }

    override fun getItemCount() = shoppingLists.size


    class ViewHolder(
        view: View,
        onClickGoToItems: (Int?) -> Unit,
        onDeleteList: (GroceryList?) -> Unit,
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

        }
    }

}