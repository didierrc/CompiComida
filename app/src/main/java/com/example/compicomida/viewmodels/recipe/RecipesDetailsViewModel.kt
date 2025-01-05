package com.example.compicomida.viewmodels.recipe

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.compicomida.CompiComidaApp
import com.example.compicomida.R
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.recipeEntities.Recipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecipesDetailsViewModel(
    private val recipesRepo: RecipeRepository,
    private val groceryRepo: GroceryRepository,
    private val pantryRepo: PantryRepository
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

    fun addRecipeToGroceryList(context: Context?) {
        _recipe.value?.let { recipe ->
            viewModelScope.launch(Dispatchers.IO) {

                // Adding a new list with the recipe name
                groceryRepo.addGroceryList(recipe.name)
                val groceryList = groceryRepo.getLastInsertedList()

                // Inserting ingredients from Recipe to the Grocery List.
                val ingredientsList = mutableListOf<GroceryItem>()
                groceryList?.let {
                    recipe.ingredients.forEach { recipeIngredient ->
                        var pantryItem = pantryRepo.getPantryItemByName(recipeIngredient.name)
                        var isInPantry = false
                        var finalQuantity = 0.0

                        if (pantryItem != null) {
                            isInPantry = true
                            finalQuantity = (recipeIngredient.quantity ?: 0.0) - pantryItem.quantity

                        } else {
                            // En caso de tener el ingrediente añadido en singular y en la receta en plural elimina la ultima letra para volver a buscarlo
                            pantryItem =
                                pantryRepo.getPantryItemByName(recipeIngredient.name.dropLast(1))
                            if (pantryItem != null) {
                                isInPantry = true
                                finalQuantity =
                                    (recipeIngredient.quantity ?: 0.0) - pantryItem.quantity
                            }
                        }

                        if (isInPantry) {
                            if (finalQuantity > 0.0) {
                                ingredientsList.add(
                                    GroceryItem(
                                        itemId = 0,
                                        listId = groceryList.listId,
                                        categoryId = null,
                                        itemName = recipeIngredient.name,
                                        quantity = finalQuantity,
                                        unit = recipeIngredient.unit
                                            ?: context?.getString(R.string.item_default_unit),
                                        price = 0.0,
                                        isPurchased = false,
                                        itemPhotoUri = CompiComidaApp.DEFAULT_GROCERY_URI
                                    )
                                )
                            }
                        } else {
                            ingredientsList.add(
                                GroceryItem(
                                    itemId = 0,
                                    listId = groceryList.listId,
                                    categoryId = null,
                                    itemName = recipeIngredient.name,
                                    quantity = recipeIngredient.quantity ?: 0.0,
                                    unit = recipeIngredient.unit
                                        ?: context?.getString(R.string.item_default_unit),
                                    price = 0.0,
                                    isPurchased = false,
                                    itemPhotoUri = CompiComidaApp.DEFAULT_GROCERY_URI
                                )
                            )
                        }
                    }
                }

                groceryRepo.addGroceryItems(ingredientsList)
            }


        }
    }
}