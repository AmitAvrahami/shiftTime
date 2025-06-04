package com.example.shiftime.domain.model

data class ScheduleStatus(
    val employees: List<Employee> = emptyList(),
    val todayShifts: List<Shift> = emptyList(),
    val nextShift: ShiftWithEmployees? = null,
    val currentShift: ShiftWithEmployees? = null,
    val activeShifts: Int = 0,
    val pendingAssignments: Int = 0,
    val time: String = "",
    val todayActiveEmployees: List<Employee> = emptyList(),
)