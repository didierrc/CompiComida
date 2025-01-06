package com.example.compicomida.model

import com.example.compicomida.model.localDb.LocalDatabase
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.PantryItem
import java.time.Clock
import java.time.LocalDateTime

class PantryRepository(
    private val db: LocalDatabase,
    private val clock: Clock = Clock.systemDefaultZone() // Clock por defecto
) {

    suspend fun getCloseExpireItems(): List<PantryItem>? = db.pantryItemDao.getCloseExpireItems()

    suspend fun getCloseExpireItems(filter: String): List<PantryItem>? {

        return when (filter) {
            "TODAY" -> db.pantryItemDao.getCloseExpireItems(LocalDateTime.now(clock))
            "TOMORROW" -> db.pantryItemDao.getCloseExpireItems(
                LocalDateTime.now(clock).plusDays(1)
            )

            "2-DAYS" -> db.pantryItemDao.getCloseExpireItems(
                LocalDateTime.now(clock).plusDays(2)
            )

            else -> db.pantryItemDao.getCloseExpireItems()
        }
    }

    suspend fun getAlreadyExpiredItems(): List<PantryItem>? =
        db.pantryItemDao.getAlreadyExpiredItems()

    suspend fun getPantryItems(): List<PantryItem>? = db.pantryItemDao.getAll()

    suspend fun addPantryItem(newPantryItem: PantryItem) = db.pantryItemDao.add(newPantryItem)

    suspend fun getPantryItemById(id: Int): PantryItem? = db.pantryItemDao.getById(id)

    private suspend fun getPantryItemByGroceryId(id: Int): PantryItem? =
        db.pantryItemDao.getByGroceryId(id)

    suspend fun getPantryItemByName(name: String): PantryItem? = db.pantryItemDao.getByName(name)

    suspend fun deletePantryItem(pantryItem: PantryItem) = db.pantryItemDao.delete(pantryItem)

    suspend fun updatePantryItem(pantryItem: PantryItem) = db.pantryItemDao.update(pantryItem)

    suspend fun addPantryItemsFromGroceryLists(groceryItem: GroceryItem) {
        val pantriItem = PantryItem(
            pantryId = 0,
            itemId = groceryItem.itemId,
            expirationDate = LocalDateTime.now(clock),
            pantryName = groceryItem.itemName,
            quantity = groceryItem.quantity,
            unit = groceryItem.unit,
            lastUpdate = LocalDateTime.now(clock),
            pantryPhotoUri = groceryItem.itemPhotoUri
        )
        addPantryItem(pantriItem)
    }

    suspend fun deletePantryItemsFromGroceryLists(groceryItem: GroceryItem) =
        deletePantryItem(getPantryItemByGroceryId(groceryItem.itemId)!!)


}