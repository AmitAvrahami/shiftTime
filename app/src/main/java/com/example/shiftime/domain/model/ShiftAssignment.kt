package com.example.shiftime.domain.model

import com.example.shiftime.utils.enums.AssignmentStatus
import java.util.Date

data class ShiftAssignment(
    val employeeId: Long,
    val shiftId: Long,
    val assignedAt: Date = Date(System.currentTimeMillis()),
    val status: AssignmentStatus = AssignmentStatus.ASSIGNED,
    val note: String? = null
)