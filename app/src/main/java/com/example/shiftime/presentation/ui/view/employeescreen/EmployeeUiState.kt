package com.example.shiftime.presentation.ui.view.employeescreen

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel

data class EmployeeUiState(
    val employees: List<EmployeeUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isAddingEmployee: Boolean = false,
    val isEditingEmployee: Boolean = false,
    val employeeToEdit: EmployeeEntity? = null,
    val showDeleteConfirmDialog: Boolean = false,
    val employeeToDelete: EmployeeUiModel? = null,
)