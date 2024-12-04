package com.example.compicomida.model

import com.example.compicomida.CompiComidaApp
import com.example.compicomida.model.recipeEntities.Recipe
import com.google.firebase.firestore.FirebaseFirestore

class RecipeRepository(
    private val db: FirebaseFirestore
) {

    fun getRecipes(callbackFx: (List<Recipe>) -> Unit) {
        db.collection(CompiComidaApp.RECIPES_COLLECTION).get()
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
}