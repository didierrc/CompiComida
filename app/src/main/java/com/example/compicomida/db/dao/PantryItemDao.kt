package com.example.compicomida.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.compicomida.db.entities.PantryItem

@Dao
interface PantryItemDao {

    // Queries

    @Query("SELECT * FROM PantryItem")
    suspend fun getAll(): List<PantryItem>

    @Query("SELECT * FROM PantryItem WHERE pantry_id = :id")
    suspend fun getById(id: Int): PantryItem?

    // Inserts

    @Insert
    suspend fun addAll(vararg pantryItems: PantryItem)

    @Insert
    suspend fun add(pantryItem: PantryItem)

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
}