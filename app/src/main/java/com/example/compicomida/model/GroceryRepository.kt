package com.example.compicomida.model

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.GroceryList
import com.example.compicomida.model.localDb.entities.ItemCategory
import java.time.LocalDateTime

class GroceryRepository(
    private val db: LocalDatabase
) {

    suspend fun getItemsFromMostRecentList(): List<GroceryItem>? {
        val recentGroceryList = db.groceryListDao.getMostRecentList()
        return recentGroceryList?.let { db.groceryItemDao.getByListId(it.listId) }
    }

    suspend fun getMostRecentListName(): String? =
        db.groceryListDao.getMostRecentList()?.listName

    suspend fun getListSize(listId: Int): Int {
        return db.groceryListDao.getListSize(listId)
    }

    suspend fun getAllLists(): List<GroceryList> {
        return db.groceryListDao.getAll()
    }

    suspend fun deleteGroceryList(it: GroceryList) {
        db.groceryListDao.delete(it)
    }

    suspend fun getGroceryItemsByListId(listId: Int): List<GroceryItem> {
        return db.groceryItemDao.getByListId(listId)
    }

    suspend fun addGroceryList(value: String) {
        db.groceryListDao.add(GroceryList(0, value, LocalDateTime.now()))
    }

    suspend fun getGroceryListByName(listName: String): GroceryList? {
        return db.groceryListDao.getByName(listName)
    }

    suspend fun addGroceryItem(newGrocery: GroceryItem) {
        db.groceryItemDao.add(newGrocery)
    }

    suspend fun getItemCategory(newCategoryName: String): ItemCategory? {
        return db.itemCategoryDao.getByName(newCategoryName)
    }

    suspend fun getGroceryItemCategoryByID(categoryID: Int): ItemCategory? {
        return db.itemCategoryDao.getById(categoryID)
    }

    suspend fun getAllCategories(): List<ItemCategory> {
        return db.itemCategoryDao.getAll()

    }

    suspend fun checkItem(checkState: Boolean, itemID: Int) {
        db.groceryItemDao.update(
            db.groceryItemDao.getById(itemID)!!.copy(isPurchased = checkState)
        )

    }

    suspend fun getGroceryItemByID(itemID: Int): GroceryItem? {
        return db.groceryItemDao.getById(itemID)
    }

    suspend fun deleteGroceryItem(itemID: Int) {
        db.groceryItemDao.delete(db.groceryItemDao.getById(itemID)!!)
    }


}