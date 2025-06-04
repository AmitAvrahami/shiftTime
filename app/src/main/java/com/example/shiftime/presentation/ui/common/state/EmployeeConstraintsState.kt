package com.example.shiftime.presentation.ui.common.state

import com.example.shiftime.domain.model.DayWithShifts
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.ShiftWithConstraint
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.utils.enums.Days
import java.time.LocalDate

data class EmployeeConstraintsState(
    val selectedEmployeeId: Long? = null,
    val selectedEmployee: Employee? = null,
    val activeWorkWeek: WorkWeek? = null,
    val selectedWorkWeekId: Long = 0L,
    val selectedDate: LocalDate? = null,
    val days: List<DayWithShifts> = emptyList(),
    val selectedDay: Days? = null,
    val selectedDayShifts: List<ShiftWithConstraint> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
