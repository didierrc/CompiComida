package com.example.compicomida.db.converters

import androidx.room.TypeConverter
import java.util.Date

// Class to convert Date to Long and vice versa.
// You can find more info: https://developer.android.com/training/data-storage/room/referencing-data
class DateConverter {

    @TypeConverter
    fun toTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

}