package com.example.shiftime.domain.model

import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType
import java.util.Date

data class Shift(
    val id: Long = 0,
    val workWeekId: Long, // הוספנו קישור לשבוע העבודה
    val shiftType: ShiftType,
    val shiftDay: Days,
    val startTime: Date,
    val endTime: Date,
    val employeesRequired: Int = 2,
    val assignedEmployees: List<Long> = emptyList() // מזהים של עובדים
)