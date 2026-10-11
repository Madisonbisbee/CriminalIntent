package com.example.criminalintent

import androidx.room.TypeConverter
import java.util.Date
import java.util.UUID

class CrimeTypeConverters {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }

    @TypeConverter
    fun fromUUID(id: UUID): String {
        return id.toString()
    }

    @TypeConverter
    fun toUUID(id: String): UUID {
        return UUID.fromString(id)
    }
}