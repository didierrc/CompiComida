package com.example.compicomida.model

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.PantryItem
import java.time.LocalDateTime

class PantryRepository(
    private val db: LocalDatabase
) {

    suspend fun getCloseExpireItems(): List<PantryItem>? = db.pantryItemDao.getCloseExpireItems()

    suspend fun getPantryItems(): List<PantryItem>? = db.pantryItemDao.getAll()

    suspend fun addPantryItem(newPantryItem: PantryItem) = db.pantryItemDao.add(newPantryItem)

    suspend fun getPantryItemById(id: Int): PantryItem? = db.pantryItemDao.getById(id)

    suspend fun getPantryItemByGroceryId(id: Int): PantryItem? = db.pantryItemDao.getByGroceryId(id)

    suspend fun deletePantryItem(pantryItem: PantryItem) = db.pantryItemDao.delete(pantryItem)

    suspend fun updatePantryItem(pantryItem: PantryItem) = db.pantryItemDao.update(pantryItem)

    suspend fun addPantryItemsFromGroceryLists(groceryItem: GroceryItem) {
        val pantriItem = PantryItem(
            pantryId = 0,
            itemId = groceryItem.itemId,
            expirationDate = LocalDateTime.now(),
            pantryName = groceryItem.itemName,
            quantity = groceryItem.quantity,
            unit = groceryItem.unit,
            lastUpdate = LocalDateTime.now(),
            pantryPhotoUri = groceryItem.itemPhotoUri
        )
        addPantryItem(pantriItem)
    }

    suspend fun deletePantryItemsFromGroceryLists(groceryItem: GroceryItem){
        deletePantryItem(getPantryItemByGroceryId(groceryItem.itemId)!!)
    }

}