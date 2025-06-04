package com.example.shiftime.presentation.ui.events.modelevents

import com.example.shiftime.domain.model.Shift
import com.example.shiftime.utils.enums.AssignmentStyle
import java.time.LocalDate

sealed class ShiftEvent {
    data class SetStartDate(val date: LocalDate) : ShiftEvent()
    data class SetSelectedDay(val dayIndex: Int) : ShiftEvent()
    data class SetAssignmentStyle(val style: AssignmentStyle) : ShiftEvent()
    data class UpdateShift(val shift: Shift) : ShiftEvent()
    data class ShowEditShiftDialog(val shift: Shift) : ShiftEvent()
    data class ActivateWorkWeek(val workWeekId: Long) : ShiftEvent()
    object TestDataExtraction : ShiftEvent()

    object HideEditShiftDialog : ShiftEvent()
    object GenerateSchedule : ShiftEvent()
    object LoadActiveWorkWeek : ShiftEvent()
    object RefreshData : ShiftEvent()
}