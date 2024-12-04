package com.example.compicomida.model.localDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.compicomida.model.localDb.converters.DateConverter
import com.example.compicomida.model.localDb.dao.GroceryItemDao
import com.example.compicomida.model.localDb.dao.GroceryListDao
import com.example.compicomida.model.localDb.dao.ItemCategoryDao
import com.example.compicomida.model.localDb.dao.PantryItemDao
import com.example.compicomida.model.localDb.entities.GroceryItem
import com.example.compicomida.model.localDb.entities.GroceryList
import com.example.compicomida.model.localDb.entities.ItemCategory
import com.example.compicomida.model.localDb.entities.PantryItem

@Database(
    entities = [GroceryList::class, GroceryItem::class, ItemCategory::class, PantryItem::class],
    version = 1, exportSchema = false
)
@TypeConverters(
    value = [DateConverter::class]
)
abstract class LocalDatabase : RoomDatabase() {

    abstract val groceryListDao: GroceryListDao
    abstract val groceryItemDao: GroceryItemDao
    abstract val itemCategoryDao: ItemCategoryDao
    abstract val pantryItemDao: PantryItemDao

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
            ).createFromAsset("LocalAppDB.db").build()

            SINGLETON = instance
            return SINGLETON as LocalDatabase
        }
    }


}