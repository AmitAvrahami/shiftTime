package com.example.shiftime.presentation.ui.common.state

import com.example.shiftime.R

data class EmployeeUiModel(
    val id: Long,
    val employeeId: String = "",
    val employeeImage: Int,
    val employeeName: String,
    val employeeDesignation: String,
    val employeePhone: String = "",
    val minShifts: Int = 0,
    val maxShifts: Int = 5,
    val isExpanded: Boolean = false
)