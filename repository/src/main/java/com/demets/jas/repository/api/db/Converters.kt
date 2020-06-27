package com.demets.jas.repository.api.db

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

class Converters {
    companion object {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return if (value == null) null else LocalDateTime.parse(value, formatter)
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.format(formatter)
    }
}
