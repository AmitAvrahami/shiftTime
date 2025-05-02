package com.example.shiftime.models

import java.util.Date

data class Shift(
    val id: String = "",
    val shiftType: ShiftType,
    val shiftDay: Days,
    val employeesId: MutableList<String> = mutableListOf(),
    val employeesRequired: Int,
    val startTime: Date,
    val endTime: Date,
    val description: String = "",
    val status: ShiftStatus = ShiftStatus.EMPTY
)



