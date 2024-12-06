package com.example.compicomida.viewmodels.recipe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.recipeEntities.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipesDetailsViewModel(
    private val recipesRepo: RecipeRepository,
    private val groceryRepo: GroceryRepository
) : ViewModel() {

    private val _recipe = MutableLiveData<Recipe?>()
    val recipe: LiveData<Recipe?>
        get() = _recipe

    fun getRecipe(recipeId: Int) {

        if (recipeId == -1) // This could not be necessary
            _recipe.postValue(null)
        else {
            recipesRepo.getRecipe(recipeId.toString()) { recipe ->
                _recipe.postValue(recipe)
            }
        }
    }

    fun addRecipeToGroceryList() {
        _recipe.value?.let { recipe ->
            viewModelScope.launch(Dispatchers.IO) {

                // Adding a new list with the recipe name
                groceryRepo.addGroceryList(recipe.name)
                val groceryList = groceryRepo.getLastInsertedList()

                // Inserting ingredients from Recipe to the Grocery List.
                val ingredientsList = mutableListOf<GroceryItem>()
                groceryList?.let {
                    recipe.ingredients.forEach { recipeIngredient ->
                        ingredientsList.add(
                            GroceryItem(
                                itemId = 0,
                                listId = groceryList.listId,
                                categoryId = null,
                                itemName = recipeIngredient.name,
                                quantity = recipeIngredient.quantity ?: 0.0,
                                unit = recipeIngredient.unit ?: "No especificada",
                                price = 0.0,
                                isPurchased = false,
                                itemPhotoUri = CompiComidaApp.DEFAULT_GROCERY_URI
                            )
                        )
                    }
                }

                groceryRepo.addGroceryItems(ingredientsList)
            }


        }
    }


}