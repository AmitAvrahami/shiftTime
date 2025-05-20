package com.example.shiftime.domain.model


import java.time.LocalDate

data class WorkWeek(
    val id: Long = 0,
    val name: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isActive: Boolean = false
)