package com.example.compicomida.model.localDb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.compicomida.model.localDb.entities.PantryItem
import java.time.LocalDateTime

@Dao
interface PantryItemDao {

    // Queries

    @Query("SELECT * FROM PantryItem")
    suspend fun getAll(): List<PantryItem>?

    @Query("SELECT * FROM PantryItem WHERE pantry_id = :id")
    suspend fun getById(id: Int): PantryItem?

    @Query(
        """ SELECT * FROM PantryItem 
            WHERE expiration_date >= DATE('now')
            AND expiration_date < DATE('now', '+3 day') """
    )
    suspend fun getCloseExpireItems(): List<PantryItem>?

    @Query("SELECT * FROM PantryItem WHERE expiration_date < DATE('now')")
    suspend fun getAlreadyExpiredItems(): List<PantryItem>?

    @Query("SELECT * FROM PantryItem WHERE DATE(expiration_date) = DATE(:date)")
    suspend fun getCloseExpireItems(date: LocalDateTime): List<PantryItem>?

    @Query("SELECT * FROM PantryItem WHERE item_id = :id")
    suspend fun getByGroceryId(id: Int): PantryItem?

    @Query("SELECT * FROM PantryItem WHERE pantry_name = :name")
    suspend fun getByName(name: String): PantryItem?

    // Inserts

    @Insert
    suspend fun addAll(vararg pantryItems: PantryItem)

    @Insert
    suspend fun add(pantryItem: PantryItem): Long?

    // Updates

    @Update
    suspend fun updateAll(vararg pantryItems: PantryItem)

    @Update
    suspend fun update(pantryItem: PantryItem)

    // Deletes

    @Delete
    suspend fun deleteAll(vararg pantryItems: PantryItem)

    @Delete
    suspend fun delete(pantryItem: PantryItem)

    @Query("DELETE FROM PantryItem")
    fun deleteAll()
}