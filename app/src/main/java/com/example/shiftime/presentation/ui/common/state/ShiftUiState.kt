package com.example.shiftime.presentation.ui.common.state

import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.utils.enums.AssignmentStyle
import com.example.shiftime.utils.enums.Days
import java.time.LocalDate

data class ShiftUiState(
    val currentWorkWeek: WorkWeek? = null,
    val startDate: LocalDate? = null,
    val selectedDay: Days = Days.SUNDAY,
    val assignmentStyle: AssignmentStyle = AssignmentStyle.BALANCED,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isEditDialogVisible: Boolean = false,
    val currentEditShift: Shift? = null,
    val allWorkWeeks: List<WorkWeek> = emptyList()
)