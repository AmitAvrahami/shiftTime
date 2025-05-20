package com.example.shiftime.data.local.converters


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

/**
 * מחלקת המרה למסד נתונים Room
 * מאפשרת המרה בין אובייקטי תאריך (Date, LocalDate) לבין ערכים שניתן לאחסן במסד
 */
class DateConverter {

    // המרה מ-Date ל-Long (שמירה במסד)
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    // המרה מ-Long ל-Date (טעינה מהמסד)
    @TypeConverter
    fun timestampToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    // המרה מ-LocalDate ל-Long (שמירה במסד)
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun localDateToTimestamp(localDate: LocalDate?): Long? {
        return localDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    // המרה מ-Long ל-LocalDate (טעינה מהמסד)
    @TypeConverter
    fun timestampToLocalDate(timestamp: Long?): LocalDate? {
        return timestamp?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }

    // המרה מ-LocalDateTime ל-Long (שמירה במסד)
    @TypeConverter
    fun localDateTimeToTimestamp(localDateTime: LocalDateTime?): Long? {
        return localDateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    // המרה מ-Long ל-LocalDateTime (טעינה מהמסד)
    @TypeConverter
    fun timestampToLocalDateTime(timestamp: Long?): LocalDateTime? {
        return timestamp?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
    }
}