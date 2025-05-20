package com.example.shiftime.data.local.converters

import androidx.room.TypeConverter
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftStatus
import com.example.shiftime.utils.enums.ShiftType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ShiftConverters {

    private val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromDate(date:Date?): String?{
        return date?.let { formatter.format(it)  }
    }

    @TypeConverter
    fun toDate(dateString: String?): Date?{
        return dateString?.let { formatter.parse(it) }
    }

    @TypeConverter
    fun fromShiftType(shiftType: ShiftType): String {
        return shiftType.name
    }

    @TypeConverter
    fun toShiftType(shiftTypeName: String): ShiftType {
        return ShiftType.valueOf(shiftTypeName)
    }

    @TypeConverter
    fun fromDays(day: Days): String = day.name

    @TypeConverter
    fun toDays(day: String): Days = Days.valueOf(day)

    @TypeConverter
    fun fromShiftStatus(status: ShiftStatus): String = status.name

    @TypeConverter
    fun toShiftStatus(status: String): ShiftStatus = ShiftStatus.valueOf(status)
}