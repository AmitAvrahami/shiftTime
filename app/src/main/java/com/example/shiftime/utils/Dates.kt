package com.example.shiftime.utils

import com.example.shiftime.utils.enums.Days
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.text.get

object HebrewDateMap {
    val monthMap = mapOf(
        "ינואר" to 1,
        "פברואר" to 2,
        "מרץ" to 3,
        "אפריל" to 4,
        "מאי" to 5,
        "יוני" to 6,
        "יולי" to 7,
        "אוגוסט" to 8,
        "ספטמבר" to 9,
        "אוקטובר" to 10,
        "נובמבר" to 11,
        "דצמבר" to 12
    )
    fun getMonthNumber(monthName: String): Int {
        return monthMap[monthName] ?: throw IllegalArgumentException("Invalid month name: $monthName")
    }

    val monthDays = mapOf(
        "ינואר" to 31, "פברואר" to 28, "מרץ" to 31, "אפריל" to 30,
        "מאי" to 31, "יוני" to 30, "יולי" to 31, "אוגוסט" to 31,
        "ספטמבר" to 30, "אוקטובר" to 31, "נובמבר" to 30, "דצמבר" to 31
    )

    fun getMonthDays(monthName: String): Int {
        return monthDays[monthName] ?: throw IllegalArgumentException("Invalid month name: $monthName")
    }

    fun generateDaysList(from : Int = 1 , to : Int = 1): List<Int> {
        return (from.until(to)).toList()
    }

    fun Date.formatToHourMinute(): String {
        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
        return formatter.format(this)
    }

    fun Date.formatToDayMonthYear(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formatter.format(this)

    }

    fun indexToDaysEnum(index: Int): Days {
        return when (index) {
            0 -> Days.SUNDAY
            1 -> Days.MONDAY
            2 -> Days.TUESDAY
            3 -> Days.WEDNESDAY
            4 -> Days.THURSDAY
            5 -> Days.FRIDAY
            6 -> Days.SATURDAY
            else -> throw IllegalArgumentException("Index must be between 0 and 6")
        }
    }


}

object DaysMapper {
     fun getCurrentDay(): Days {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> Days.SUNDAY
            Calendar.MONDAY -> Days.MONDAY
            Calendar.TUESDAY -> Days.TUESDAY
            Calendar.WEDNESDAY -> Days.WEDNESDAY
            Calendar.THURSDAY -> Days.THURSDAY
            Calendar.FRIDAY -> Days.FRIDAY
            Calendar.SATURDAY -> Days.SATURDAY
            else -> Days.SUNDAY
        }
    }
}