package com.example.compicomida.model.localDb.converters

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Class to convert Date to Long and vice versa.
class DateConverter {

    @TypeConverter
    fun toTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    fun fromTimestampWithOutHours(value: String?): LocalDateTime? {
        val aux = "$value 00:00:00"
        return aux.let {
            LocalDateTime.parse(it, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        }
    }

}