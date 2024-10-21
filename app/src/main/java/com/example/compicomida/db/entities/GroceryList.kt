package com.example.compicomida.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class GroceryList(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "list_id")
    var listId: Int,

    @ColumnInfo(name = "list_name") val listName: String,
    @ColumnInfo(name = "created_at") val createdAt: LocalDateTime
)