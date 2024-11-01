package com.example.compicomida.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.compicomida.db.entities.ItemCategory

@Dao
interface ItemCategoryDao {

    // Queries

    @Query("SELECT * FROM ItemCategory")
    suspend fun getAll(): List<ItemCategory>

    @Query("SELECT * FROM ItemCategory WHERE category_id = :id")
    suspend fun getById(id: Int): ItemCategory?

    @Query("SELECT * FROM ItemCategory WHERE category_name = :name")
    suspend fun getByName(name: String): ItemCategory?

    // Inserts

    @Insert
    suspend fun addAll(vararg itemCats: ItemCategory)

    @Insert
    suspend fun add(itemCat: ItemCategory)

    // Updates

    @Update
    suspend fun updateAll(vararg itemCats: ItemCategory)

    @Update
    suspend fun update(itemCat: ItemCategory)

    // Deletes

    @Delete
    suspend fun deleteAll(vararg itemCats: ItemCategory)

    @Delete
    suspend fun delete(itemCat: ItemCategory)

}