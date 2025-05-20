package com.example.shiftime.domain.model

data class EmployeeWithAssignedShifts(
    val employee: Employee,
    val shifts: List<Shift>
)