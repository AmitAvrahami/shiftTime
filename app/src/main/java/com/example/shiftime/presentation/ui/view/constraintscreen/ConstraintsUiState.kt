package com.example.shiftime.presentation.ui.view.constraintscreen

import com.example.shiftime.utils.enums.ShiftType

data class ConstraintsUiState(
    val selectedEmployeeId : String? = null,
    val selectedEmployeeName: String = "",
    val unavailableShifts: Set<UnavailableShift> = emptySet(),
    val isLoading: Boolean = false,
    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean? = null
){
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("selectedEmployeeId: $selectedEmployeeId")
        sb.append("selectedEmployeeName: $selectedEmployeeName")
        sb.append("unavailableShifts: $unavailableShifts")
        sb.append("isLoading: $isLoading")
        sb.append("isSubmitting: $isSubmitting")
        sb.append("submissionSuccess: $submissionSuccess")
        return sb.toString()
    }
}

data class UnavailableShift(
    val date: String,
    val shiftType: ShiftType
)
{
    override fun toString(): String {
        return "UnavailableShift(date='$date', shiftType=$shiftType)"
    }
}
