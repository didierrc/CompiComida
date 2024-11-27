package com.example.compicomida

import android.app.Application
import android.content.Context
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.PantryRepository
import com.example.compicomida.model.RecipeRepository
import com.example.compicomida.model.localDb.LocalDatabase
import com.google.firebase.firestore.FirebaseFirestore

class CompiComidaApp : Application() {

    companion object {
        lateinit var appModule: AppModule
        const val RECIPES_COLLECTION = "recipes"
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
    }
}

interface AppModule {
    val localDb: LocalDatabase
    val recipesDb: FirebaseFirestore
    val pantryRepo: PantryRepository
    val groceryRepo: GroceryRepository
    val recipesRepo: RecipeRepository
}

class AppModuleImpl(
    private val context: Context
) : AppModule {

    // Repository - Pantry, Grocery, Recipes
    override val pantryRepo: PantryRepository by lazy {
        PantryRepository(localDb)
    }

    override val groceryRepo: GroceryRepository by lazy {
        GroceryRepository(localDb)
    }

    override val recipesRepo: RecipeRepository by lazy {
        RecipeRepository(recipesDb)
    }

    // Room - Local Database initialisation
    override val localDb: LocalDatabase by lazy {
        LocalDatabase.getDB(context)
    }

    // Firestore - Firebase initialisation
    override val recipesDb: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }
}