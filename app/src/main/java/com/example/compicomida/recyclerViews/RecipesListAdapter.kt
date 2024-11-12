package com.example.compicomida.recyclerViews

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.recyclerViews.ShoppingListsAdapter.ViewHolder
import com.google.firebase.firestore.DocumentSnapshot
import net.nicbell.materiallists.ListItem

/**
 * Creates a view of recipes list to be used
 * in a Recycler View.
 */
class RecipesListAdapter(
    private val recipesList: List<DocumentSnapshot>,
    private val onClickGoToDetails: (Int?) -> Unit

) : RecyclerView.Adapter<RecipesListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val layoutItem = R.layout.recycler_recipes_list
        val view =
            LayoutInflater.from(viewGroup.context).inflate(layoutItem, viewGroup, false)
        return ViewHolder(view, onClickGoToDetails)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(recipesList[position])
    }

    override fun getItemCount() = recipesList.size

    class ViewHolder(
        view: View,
        onClickGoToDetails: (Int?) -> Unit
    ) : RecyclerView.ViewHolder(view) {

        private val listItem = view.findViewById<ListItem>(R.id.recipeElementItem)
        private val listItemImage =
            view.findViewById<AppCompatImageView>(R.id.recipeElementItemImage)
        private var recipeId: Int? = null

        init {
            view.setOnClickListener {
                onClickGoToDetails(recipeId)
            }
        }

        fun bind(recipe: DocumentSnapshot) {
            recipeId = recipe.id.toInt()

            listItem.headline.text = recipe.get("name") as String
            listItem.supportText.text =
                "${(recipe.get("description") as String).substring(0, 55)} ..."
            listItemImage.load(recipe.get("imageUrl"))


        }
    }
}