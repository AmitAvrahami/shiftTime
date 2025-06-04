package com.example.shiftime.domain.model

data class ShiftWithEmployees(
    val shift: Shift,
    val employees: List<Employee>
)