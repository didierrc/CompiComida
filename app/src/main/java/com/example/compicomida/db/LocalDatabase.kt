package com.example.compicomida.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.compicomida.db.converters.DateConverter
import com.example.compicomida.db.dao.GroceryItemDao
import com.example.compicomida.db.dao.GroceryListDao
import com.example.compicomida.db.dao.ItemCategoryDao
import com.example.compicomida.db.dao.PantryItemDao
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList
import com.example.compicomida.db.entities.ItemCategory
import com.example.compicomida.db.entities.PantryItem

@Database(
    entities = [GroceryList::class, GroceryItem::class, ItemCategory::class, PantryItem::class],
    version = 1, exportSchema = false
)
@TypeConverters(
    value = [DateConverter::class]
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun groceryListDao(): GroceryListDao
    abstract fun groceryItemDao(): GroceryItemDao
    abstract fun itemCategoryDao(): ItemCategoryDao
    abstract fun pantryItemDao(): PantryItemDao

    // Singleton database
    companion object {
        @Volatile
        private var SINGLETON: LocalDatabase? = null

        fun getDB(context: Context?): LocalDatabase {
            if (SINGLETON != null)
                return SINGLETON as LocalDatabase

            val instance = Room.databaseBuilder(
                context!!.applicationContext,
                LocalDatabase::class.java,
                "LocalAppDB"
            ).build()

            SINGLETON = instance
            return SINGLETON as LocalDatabase
        }
    }


}