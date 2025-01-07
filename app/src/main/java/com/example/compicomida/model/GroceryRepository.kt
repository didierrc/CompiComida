package com.example.compicomida.model

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.dao.GroceryItemDao
import com.example.compicomida.model.localDb.dao.GroceryListDao
import com.example.compicomida.model.localDb.dao.ItemCategoryDao
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.GroceryList
import com.example.compicomida.model.localDb.entities.ItemCategory
import java.time.Clock
import java.time.LocalDateTime

class GroceryRepository(
    private val groceryListDao: GroceryListDao,
    private val groceryItemDao: GroceryItemDao,
    private val itemCategoryDao: ItemCategoryDao,
    private val clock: Clock = Clock.systemDefaultZone() // Clock por defecto
) {

    suspend fun getListSize(listId: Int): Int {
        return groceryListDao.getListSize(listId)
    }

    suspend fun getLastInsertedList(): GroceryList? {
        return groceryListDao.getLastInserted()
    }

    suspend fun getAllLists(): List<GroceryList> {
        return groceryListDao.getAll()
    }

    suspend fun deleteGroceryList(it: GroceryList) {
        groceryListDao.delete(it)
    }

    suspend fun getGroceryItemsByListId(listId: Int): List<GroceryItem> {
        return groceryItemDao.getByListId(listId)
    }

    suspend fun addGroceryList(value: String) {
        groceryListDao.add(GroceryList(0, value, LocalDateTime.now(clock)))
    }

    suspend fun getGroceryListByName(listName: String): GroceryList? {
        return groceryListDao.getByName(listName)
    }

    suspend fun addGroceryItem(newGrocery: GroceryItem) {
        groceryItemDao.add(newGrocery)
    }

    suspend fun getItemCategory(newCategoryName: String): ItemCategory? {
        return itemCategoryDao.getByName(newCategoryName)
    }

    suspend fun getGroceryItemCategoryByID(categoryID: Int): ItemCategory? {
        return itemCategoryDao.getById(categoryID)
    }

    suspend fun getAllCategories(): List<ItemCategory> {
        return itemCategoryDao.getAll()

    }

    suspend fun checkGroceryItem(checkState: Boolean, itemID: Int) {
        groceryItemDao.update(
            groceryItemDao.getById(itemID)!!.copy(isPurchased = checkState)
        )
    }

    suspend fun getGroceryItemByID(itemID: Int): GroceryItem? {
        return groceryItemDao.getById(itemID)
    }

    suspend fun deleteGroceryItemByID(itemID: Int) {
        groceryItemDao.delete(groceryItemDao.getById(itemID)!!)
    }

    suspend fun getGroceryListByID(listID: Int): GroceryList? {
        return groceryListDao.getById(listID)
    }

    suspend fun addGroceryItems(groceryItems: List<GroceryItem>) {
        groceryItemDao.addAll(*groceryItems.toTypedArray())
    }


}