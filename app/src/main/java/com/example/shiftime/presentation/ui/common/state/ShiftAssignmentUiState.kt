package com.example.shiftime.presentation.ui.common.state

import com.example.shiftime.domain.model.EmployeeWithAssignedShifts
import com.example.shiftime.domain.model.ShiftWithAssignedEmployees

data class ShiftAssignmentUiState(
    val currentShiftWithEmployees: ShiftWithAssignedEmployees? = null,
    val currentEmployeeWithShifts: EmployeeWithAssignedShifts? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)