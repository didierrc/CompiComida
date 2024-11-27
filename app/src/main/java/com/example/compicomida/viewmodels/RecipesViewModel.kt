package com.example.compicomida.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.model.recipeEntities.Recipe

class RecipesViewModel(
    private val repo: RecipeRepository
) : ViewModel() {

    private val _recipesList = MutableLiveData<List<Recipe>>()
    val recipesList: LiveData<List<Recipe>>
        get() = _recipesList

    fun refreshRecipesList() {
        repo.getRecipes { recipeList ->
            _recipesList.postValue(recipeList)
        }
    }

}