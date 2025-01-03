package com.example.compicomida.viewmodels.home.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.viewmodels.home.SuggestedRecipeTabViewModel

class SuggestedRecipeTabViewModelFactory(
    private val recipeRepo: RecipeRepository,
    private val groceryRepo: GroceryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SuggestedRecipeTabViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SuggestedRecipeTabViewModel(recipeRepo, groceryRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

}