package com.example.compicomida

import android.app.Application
import android.content.Context
import com.example.compicomida.model.GroceryRepository
import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.PantryRepository
import com.google.firebase.firestore.FirebaseFirestore

class CompiComidaApp : Application() {

    companion object {
        lateinit var appModule: AppModule
        val RECIPES_COLLECTION = "recipes"
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
}

class AppModuleImpl(
    private val context: Context
) : AppModule {

    // Repository - Pantry, Grocery
    override val pantryRepo: PantryRepository by lazy {
        PantryRepository(localDb)
    }

    override val groceryRepo: GroceryRepository by lazy {
        GroceryRepository(localDb)
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