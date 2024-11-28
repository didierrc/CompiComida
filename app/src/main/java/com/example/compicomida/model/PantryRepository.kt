package com.example.compicomida.model

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.PantryItem

class PantryRepository(
    private val db: LocalDatabase
) {

    suspend fun getCloseExpireItems(): List<PantryItem>? = db.pantryItemDao.getCloseExpireItems()

    suspend fun getPantryItems(): List<PantryItem>? = db.pantryItemDao.getAll()

    suspend fun addPantryItem(newPantryItem: PantryItem) = db.pantryItemDao.add(newPantryItem)
    
}