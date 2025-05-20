package com.example.shiftime.presentation.ui.view.employeescreen

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel

sealed class EmployeeEvent {
    data class AddEmployee(val employee: EmployeeEntity) : EmployeeEvent()
    data class DeleteEmployee(val employee: EmployeeUiModel) : EmployeeEvent()
    data class UpdateEmployee(val employee: EmployeeEntity) : EmployeeEvent()
    data object ShowAddEmployeeDialog : EmployeeEvent()
    data object HideAddEmployeeDialog : EmployeeEvent()
    data class ToggleEmployeeDetails(val employeeId: String) : EmployeeEvent()
    data class ShowEditDialog(val employee: EmployeeUiModel) : EmployeeEvent()
    data object HideEditDialog : EmployeeEvent()
    data class ShowDeleteConfirmDialog(val employee: EmployeeUiModel) : EmployeeEvent()
    data object HideDeleteConfirmDialog : EmployeeEvent()
}