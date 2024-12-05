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

    suspend fun deletePantryItem(pantryItem: PantryItem) = db.pantryItemDao.delete(pantryItem)

    suspend fun updatePantryItem(pantryItem: PantryItem) = db.pantryItemDao.update(pantryItem)

    suspend fun addPantryItemsFromGroceryLists(list: List<GroceryItem>) {
        list.forEach{ item ->
            if(item.isPurchased){
                val pantriItem = PantryItem(
                    pantryId = 0,
                    itemId = null,
                    expirationDate = LocalDateTime.now(),
                    pantryName = item.itemName,
                    quantity = item.quantity,
                    unit = item.unit,
                    lastUpdate = LocalDateTime.now(),
                    pantryPhotoUri = item.itemPhotoUri
                )
                addPantryItem(pantriItem)
            }
        }
    }

}