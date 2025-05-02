package com.example.shiftime.utils

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
}