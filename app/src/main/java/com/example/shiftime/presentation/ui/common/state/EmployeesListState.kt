package com.example.shiftime.presentation.ui.common.state

data class EmployeesListState(
    val employees: List<EmployeeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
