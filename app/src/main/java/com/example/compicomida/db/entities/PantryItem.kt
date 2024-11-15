package com.example.compicomida.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class PantryItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "pantry_id")
    var pantryId: Int,

    // Foreign key to the GroceryItem entity
    // You can add items to your pantry from outside a list.
    @ColumnInfo(name = "item_id") val itemId: Int?,

    // Other attributes
    @ColumnInfo(name = "expiration_date") val expirationDate: LocalDateTime,
    @ColumnInfo(name = "pantry_name") val pantryName: String,
    val quantity: Double,
    val unit: String?, // nullable
    @ColumnInfo(name = "last_update") val lastUpdate: LocalDateTime,
    // Storing photo URI instead of the photo itself as Blob.
    @ColumnInfo(name = "pantry_photo_uri") val pantryPhotoUri: String?
)
