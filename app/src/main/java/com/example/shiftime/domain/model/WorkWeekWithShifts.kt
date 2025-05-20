package com.example.shiftime.domain.model

data class WorkWeekWithShifts(
    val workWeek: WorkWeek,
    val shifts: List<Shift>
)