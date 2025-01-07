package com.example.compicomida

import com.example.compicomida.model.localDb.converters.DateConverter
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDateTime
import java.time.format.DateTimeParseException

class DateConverterTest {
    private val dateConverter = DateConverter()

    @Test
    fun `toTimestamp should convert LocalDateTime to String`() {
        val localDateTime = LocalDateTime.of(2025, 1, 3, 15, 30)
        val result = dateConverter.toTimestamp(localDateTime)
        assertEquals("2025-01-03T15:30", result)
    }

    @Test
    fun `toTimestamp should return null for null input`() {
        val result = dateConverter.toTimestamp(null)
        assertNull(result)
    }

    @Test
    fun `fromTimestamp should convert String to LocalDateTime`() {
        val timestamp = "2025-01-03T15:30"
        val result = dateConverter.fromTimestamp(timestamp)
        assertEquals(LocalDateTime.of(2025, 1, 3, 15, 30), result)
    }

    @Test
    fun `fromTimestamp should return null for null input`() {
        val result = dateConverter.fromTimestamp(null)
        assertNull(result)
    }

    @Test(expected = DateTimeParseException::class)
    fun `fromTimestamp should throw DateTimeParseException for invalid format`() {
        val invalidTimestamp = "2025-01-03 15:30" // Incorrect format (space instead of T)
        dateConverter.fromTimestamp(invalidTimestamp)
    }

    @Test
    fun `fromTimestampWithOutHours should convert String date to LocalDateTime at start of day`() {
        val date = "03/01/2025"
        val result = dateConverter.fromTimestampWithOutHours(date)
        assertEquals(LocalDateTime.of(2025, 1, 3, 0, 0), result)
    }

    @Test(expected = DateTimeParseException::class)
    fun `fromTimestampWithOutHours should throw DateTimeParseException for invalid input`() {
        val invalidDate = "2025-01-03" // Incorrect format
        dateConverter.fromTimestampWithOutHours(invalidDate)
    }
}