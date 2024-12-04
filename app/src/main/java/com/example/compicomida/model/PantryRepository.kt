package com.example.compicomida.model

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.PantryItem

class PantryRepository(
    private val db: LocalDatabase
) {

    suspend fun getCloseExpireItems(): List<PantryItem>? = db.pantryItemDao.getCloseExpireItems()

    suspend fun getPantryItems(): List<PantryItem>? = db.pantryItemDao.getAll()

    suspend fun addPantryItem(newPantryItem: PantryItem) = db.pantryItemDao.add(newPantryItem)

    suspend fun getPantryItemById(id: Int): PantryItem? = db.pantryItemDao.getById(id)

    suspend fun deletePantryItem(pantryItem: PantryItem) = db.pantryItemDao.delete(pantryItem)

    suspend fun updatePantryItem(pantryItem: PantryItem) = db.pantryItemDao.update(pantryItem)

}