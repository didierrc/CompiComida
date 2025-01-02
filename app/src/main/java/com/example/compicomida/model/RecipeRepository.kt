package com.example.compicomida.model

import android.util.Log
import com.example.compicomida.CompiComidaApp.Companion.RECIPES_COLLECTION
import com.example.compicomida.CompiComidaApp.Companion.RECIPES_COLLECTION_EN
import com.example.compicomida.model.recipeEntities.Recipe
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class RecipeRepository(
    private val db: FirebaseFirestore
) {

    private fun getCollectionNameBasedOnLanguage(): String {
        val currentLanguage = Locale.getDefault().language
        return if (currentLanguage == "es") {
            RECIPES_COLLECTION
        } else {
            RECIPES_COLLECTION_EN
        }
    }

    fun getRecipes(callbackFx: (List<Recipe>) -> Unit) {
        db.collection(getCollectionNameBasedOnLanguage()).get()
            .addOnSuccessListener {
                val recipes = it.documents.mapNotNull { recipe ->
                    recipe.toObject(Recipe::class.java)?.copy(id = recipe.id)
                }
                callbackFx(recipes)
            }
            .addOnFailureListener {
                callbackFx(listOf())
            }
    }

    fun getRecipe(recipeId: String, callbackFx: (Recipe?) -> Unit) {
        db.collection(getCollectionNameBasedOnLanguage()).document(recipeId).get()
            .addOnSuccessListener {
                val recipe = it.toObject(Recipe::class.java)?.copy(id = it.id)
                callbackFx(recipe)
            }.addOnFailureListener {
                Log.d("Recipe Details - FAIL", "No such document")
                callbackFx(null)
            }
    }


    fun getRandomRecipe(callbackFx: (Recipe?) -> Unit) {
        db.collection(getCollectionNameBasedOnLanguage()).get()
            .addOnSuccessListener {
                val recipes = it.documents.mapNotNull { recipe ->
                    recipe.toObject(Recipe::class.java)?.copy(id = recipe.id)
                }
                callbackFx(recipes.random())
            }.addOnFailureListener {
                Log.d("Recipe Random - FAIL", "No such document")
                callbackFx(null)
            }
    }
}