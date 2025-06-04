package com.example.shiftime.presentation.ui.events.modelevents

import com.example.shiftime.utils.enums.Days
import java.time.LocalDate

sealed class EmployeeConstraintsEvent {
    data class SelectEmployee(val employeeId: Long) : EmployeeConstraintsEvent()
    data class SelectDay(val day: Days) : EmployeeConstraintsEvent()
    data class ToggleCanWork(val shiftId: Long, val canWork: Boolean, val comment: String?) : EmployeeConstraintsEvent()
    data class DeleteConstraint(val shiftId: Long) : EmployeeConstraintsEvent()
    data class ChangeMonth(val newDate: LocalDate) : EmployeeConstraintsEvent()
    object NavigateBack : EmployeeConstraintsEvent()
}