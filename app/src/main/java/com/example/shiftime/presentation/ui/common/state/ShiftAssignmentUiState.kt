package com.example.shiftime.presentation.ui.common.state

import com.example.shiftime.domain.model.EmployeeWithShifts
import com.example.shiftime.domain.model.ShiftWithEmployees

data class ShiftAssignmentUiState(
    val currentShiftWithEmployees: ShiftWithEmployees? = null,
    val currentEmployeeWithShifts: EmployeeWithShifts? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)