package com.example.compicomida.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList

@Dao
interface GroceryListDao {

    // Queries

    @Query("SELECT * FROM GroceryList")
    suspend fun getAll(): List<GroceryList>

    @Query("SELECT * FROM GroceryList WHERE list_id = :id")
    suspend fun getById(id: Int): GroceryList?

    @Query("SELECT * FROM GroceryList WHERE list_name = :name")
    suspend fun getByName(name: String): GroceryList?

    @Query(
        "SELECT * FROM GroceryList g " +
                "INNER JOIN GroceryItem i on g.list_id = i.list_id"
    )
    suspend fun getAllWithItems(): Map<GroceryList, List<GroceryItem>>

    @Query("SELECT * FROM GroceryList ORDER BY list_id DESC LIMIT 1")
    suspend fun getLastInserted(): GroceryList?

    @Query("SELECT COUNT(*) FROM GroceryItem WHERE list_id = :id")
    suspend fun getListSize(id: Int): Int

    // Inserts

    @Insert
    suspend fun addAll(vararg groceryLists: GroceryList)

    @Insert
    suspend fun add(groceryList: GroceryList)

    // Updates

    @Update
    suspend fun updateAll(vararg groceryLists: GroceryList)

    @Update
    suspend fun update(groceryList: GroceryList)

    // Deletes

    @Delete
    suspend fun deleteAll(vararg groceryLists: GroceryList)

    @Delete
    suspend fun delete(groceryList: GroceryList)
}