package com.example.shiftime.domain.model

data class ShiftWithAssignedEmployees(
    val shift: Shift,
    val employees: List<Employee>
)