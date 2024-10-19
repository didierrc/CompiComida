package com.example.compicomida.db.entities.relationships

import androidx.room.Embedded
import androidx.room.Relation
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList

// Based on: https://developer.android.com/training/data-storage/room/relationships#one-to-many
data class GroceryListWithItems(
    @Embedded val groceryList: GroceryList,
    @Relation(
        parentColumn = "list_id",
        entityColumn = "list_id"
    )
    val items: List<GroceryItem>
)
