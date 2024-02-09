package com.example.mydailyapp.converters

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date

class Converters {

//    @TypeConverters
//    fun fromTimestamp(value: Long): Date {
//        return Date(value)
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: Date): Long {
//        return date.time
//    }

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}