package com.example.compicomida.db.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime

// Class to convert Date to Long and vice versa.
// You can find more info: https://developer.android.com/training/data-storage/room/referencing-data
class DateConverter {

    @TypeConverter
    fun toTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

}