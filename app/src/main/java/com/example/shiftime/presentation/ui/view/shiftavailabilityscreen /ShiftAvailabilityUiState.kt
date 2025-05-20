package com.example.shiftime.ui.view.shiftavailabilityscreen

import com.example.shiftime.presentation.ui.view.constraintscreen.UnavailableShift

data class ShiftAvailabilityUiState(
    val selectedEmployeeId: String = "",
    val selectedMonth: String = "ינואר",
    val selectedDay: Int = 1,
    val fromDay: Int = 0,
    val toDay: Int = 7,
    val unavailableShifts: Set<UnavailableShift> = emptySet()
)