package com.example.shiftime.presentation.ui.events.modelevents

import com.example.shiftime.utils.enums.AssignmentStatus

sealed class ShiftAssignmentEvent {
    data class AssignEmployeeToShift(val employeeId: Long, val shiftId: Long, val note: String? = null) : ShiftAssignmentEvent()
    data class RemoveEmployeeFromShift(val employeeId: Long, val shiftId: Long) : ShiftAssignmentEvent()
    data class UpdateAssignmentStatus(val employeeId: Long, val shiftId: Long, val status: AssignmentStatus) : ShiftAssignmentEvent()
    data class LoadShiftWithEmployees(val shiftId: Long) : ShiftAssignmentEvent()
    data class LoadEmployeeWithShifts(val employeeId: Long) : ShiftAssignmentEvent()
}