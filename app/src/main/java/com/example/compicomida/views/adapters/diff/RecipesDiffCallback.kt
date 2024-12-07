package com.example.compicomida.views.adapters.diff

import androidx.recyclerview.widget.DiffUtil
import com.example.compicomida.model.recipeEntities.Recipe

class RecipesDiffCallback(
    private val oldRecipesList: List<Recipe>,
    private val newRecipesList: List<Recipe>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldRecipesList.size

    override fun getNewListSize(): Int = newRecipesList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldRecipesList[oldItemPosition].id == newRecipesList[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldRecipe = oldRecipesList[oldItemPosition]
        val newRecipe = newRecipesList[newItemPosition]

        return oldRecipe.name == newRecipe.name &&
                oldRecipe.imageUrl == newRecipe.imageUrl &&
                oldRecipe.category == newRecipe.category &&
                oldRecipe.preparationTime == newRecipe.preparationTime &&
                oldRecipe.difficulty == newRecipe.difficulty &&
                oldRecipe.description == newRecipe.description &&
                oldRecipe.diner == newRecipe.diner &&
                oldRecipe.ingredients.containsAll(newRecipe.ingredients) &&
                oldRecipe.steps.containsAll(newRecipe.steps)

    }

}