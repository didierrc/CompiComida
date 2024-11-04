package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.db.entities.GroceryList
import java.time.format.DateTimeFormatter

/**
 * Creates a view of a shopping list to be used
 * in a Recycler View.
 */
class ShoppingListsAdapter(

    private val shoppingLists: List<GroceryList>,
    private val onClickGoToItems: (Int?) -> Unit

) : RecyclerView.Adapter<ShoppingListsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = R.layout.recycler_grocery_list
        val view = LayoutInflater.from(viewGroup.context).inflate(layoutItem, viewGroup, false)
        return ViewHolder(view, onClickGoToItems)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(shoppingLists[position])
    }

    override fun getItemCount() = shoppingLists.size


    class ViewHolder(
        view: View,
        onClickGoToItems: (Int?) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val tvName: TextView = view.findViewById(R.id.recycler_grocery_list_name)
        private val tvDate: TextView = view.findViewById(R.id.recycler_grocery_list_date)
        private val imageView: ImageView = view.findViewById(R.id.recycler_grocery_list_image)
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

        private var groceryList: GroceryList? = null


        init {
            view.setOnClickListener {
                onClickGoToItems(groceryList?.listId)
            }
        }

        fun bind(groceryList: GroceryList) {
            this.groceryList = groceryList

            tvName.text = groceryList.listName
            tvDate.text = itemView.context.getString(
                R.string.recycler_grocery_list_date,
                groceryList.createdAt.format(dateTimeFormatter)
            )
            // TODO: Change in the future for Users' image.
            imageView.load("https://cdn-icons-png.flaticon.com/512/7835/7835565.png")
        }
    }

}