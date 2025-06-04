package com.example.shiftime.domain.model

data class EmployeeWithShifts(
    val employee: Employee,
    val shifts: List<Shift>
)