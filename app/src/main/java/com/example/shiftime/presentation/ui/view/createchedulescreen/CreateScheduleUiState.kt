package com.example.shiftime.presentation.ui.view.createchedulescreen


import com.example.shiftime.domain.model.Shift
import com.example.shiftime.utils.enums.AssignmentStyle
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType
import java.time.LocalDate

data class ShiftDetails(
    val shiftType: ShiftType,
    val startTime: String,
    val endTime: String
)
data class CreateScheduleUiState(
    val selectedSunday: LocalDate? = null,
    val assignmentStyle: AssignmentStyle = AssignmentStyle.BALANCED,
    val shiftsPerDay: Map<ShiftType, ShiftDetails> = defaultShifts(),
    val selectedDay: Days? = Days.SUNDAY,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)