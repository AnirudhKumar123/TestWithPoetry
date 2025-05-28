package com.example.testwithpoetry.data.local.database

import androidx.room.TypeConverter
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimesStamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimesStamp(date: Date?): Long? {
        return date?.time
    }
}