package com.example.compicomida.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.compicomida.db.converters.DateConverter
import com.example.compicomida.db.dao.GroceryItemDao
import com.example.compicomida.db.dao.GroceryListDao
import com.example.compicomida.db.entities.GroceryItem
import com.example.compicomida.db.entities.GroceryList

@Database(
    entities = [GroceryList::class, GroceryItem::class],
    version = 1, exportSchema = false
)
@TypeConverters(
    value = [DateConverter::class]
)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun groceryListDao(): GroceryListDao
    abstract fun groceryItemDao(): GroceryItemDao

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