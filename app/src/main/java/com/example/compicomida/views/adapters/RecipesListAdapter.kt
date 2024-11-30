package com.example.compicomida.views.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.example.compicomida.R
import com.example.compicomida.model.recipeEntities.Recipe
import net.nicbell.materiallists.ListItem

/**
 * Creates a view of recipes list to be used
 * in a Recycler View.
 */
class RecipesListAdapter(
    private var recipesList: List<Recipe>,
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

    fun updateList(recipesList: List<Recipe>) {
        this.recipesList = recipesList
        notifyDataSetChanged()
    }

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

        fun bind(recipe: Recipe) {

            recipeId = recipe.id?.toInt()

            listItem.headline.text = recipe.name
            listItem.supportText.text = "${recipe.description.substring(0, 55)} ..."
            listItemImage.load(recipe.imageUrl)


        }
    }
}