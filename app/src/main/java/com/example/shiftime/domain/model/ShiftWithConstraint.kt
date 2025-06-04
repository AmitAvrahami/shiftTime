package com.example.shiftime.domain.model

data class ShiftWithConstraint(
    val shift: Shift,
    val constraint: EmployeeConstraint? = null
) {
    val canWork: Boolean get() = constraint?.canWork != false
    val hasConstraint: Boolean get() = constraint != null
}