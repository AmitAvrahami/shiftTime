package com.example.shiftime.domain.model

import com.example.shiftime.utils.enums.Days
import java.time.LocalDate

data class DayWithShifts(
    val day: Days,
    val date: LocalDate,
    val shifts: List<ShiftWithConstraint>
)