package com.example.shiftime.domain.model

import java.util.Date

data class EmployeeConstraint(
    val employeeId: Long,
    val shiftId: Long,
    val canWork: Boolean,
    val comment: String? = null,
    val createdAt: Date = Date()
)