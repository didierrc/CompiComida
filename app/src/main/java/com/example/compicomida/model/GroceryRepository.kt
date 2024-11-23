package com.example.compicomida.model

import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.LocalDatabase

class GroceryRepository(
    private val db: LocalDatabase
) {

    suspend fun getItemsFromMostRecentList(): List<GroceryItem>? {
        val recentGroceryList = db.groceryListDao.getMostRecentList()
        return recentGroceryList?.let { db.groceryItemDao.getByListId(it.listId) }
    }

    suspend fun getMostRecentListName(): String? = db.groceryListDao.getMostRecentList()?.listName


}