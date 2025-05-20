package com.example.shiftime.domain.model

import com.example.shiftime.domain.model.Shift
import com.example.shiftime.utils.enums.Role
import java.util.Date

// domain/model/Employee.kt
data class Employee(
    val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val idNumber: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val dateOfBirth: Date = Date(),
    val maxShifts: Int = 5,
    val minShifts: Int = 0,
    val totalWorkHoursLimit: Double = 40.0,
    val role: Role = Role.REGULAR,

    val assignedShiftIds: List<Long> = emptyList(),
    val unavailableShiftIds: List<Long> = emptyList()
)

