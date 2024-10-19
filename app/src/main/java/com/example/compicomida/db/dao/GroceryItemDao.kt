package com.example.compicomida.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.compicomida.db.entities.GroceryItem

@Dao
interface GroceryItemDao {

    // Queries

    @Query("SELECT * FROM GroceryItem")
    suspend fun getAll(): List<GroceryItem>

    @Query("SELECT * FROM GroceryItem WHERE item_id = :id")
    suspend fun getById(id: Int): GroceryItem?

    // Inserts

    @Insert
    suspend fun addAll(vararg groceryItems: GroceryItem)

    @Insert
    suspend fun add(groceryItem: GroceryItem)

    // Updates

    @Update
    suspend fun updateAll(vararg groceryItems: GroceryItem)

    @Update
    suspend fun update(groceryItem: GroceryItem)

    // Deletes

    @Delete
    suspend fun deleteAll(vararg groceryItems: GroceryItem)

    @Delete
    suspend fun delete(groceryItem: GroceryItem)


}