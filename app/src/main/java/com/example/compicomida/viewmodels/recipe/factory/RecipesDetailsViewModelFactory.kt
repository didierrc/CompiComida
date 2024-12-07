package com.example.compicomida.viewmodels.recipe.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.viewmodels.recipe.RecipesDetailsViewModel

class RecipesDetailsViewModelFactory(
    private val recipesRepo: RecipeRepository,
    private val groceryRepo: GroceryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecipesDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RecipesDetailsViewModel(recipesRepo, groceryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }


}