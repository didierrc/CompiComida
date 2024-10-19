package com.example.compicomida.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = GroceryList::class,
            parentColumns = ["list_id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ItemCategory::class,
            parentColumns = ["category_id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ], indices = [
        Index(value = ["list_id"])
    ]
)
data class GroceryItem(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "item_id")
    var itemId: Int,

    // Foreign key to the GroceryList entity
    @ColumnInfo(name = "list_id") val listId: Int,

    // Foreign key to the ItemCategory entity
    @ColumnInfo(name = "category_id") val categoryId: Int?,

    // Other attributes
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "expiration_date") val expirationDate: Date,
    val quantity: Int,
    val unit: String?, // nullable
    val price: Double,
    @ColumnInfo(name = "is_purchased") val isPurchased: Boolean,
    // Storing photo URI instead of the photo itself as Blob.
    @ColumnInfo(name = "item_photo_uri") val itemPhotoUri: String?
)