package com.example.shiftime.presentation.ui.view.createchedulescreen

import com.example.shiftime.utils.enums.ShiftType


fun defaultShifts(): Map<ShiftType, ShiftDetails> {
    return mapOf(
        ShiftType.MORNING to ShiftDetails(ShiftType.MORNING, "06:45", "14:45"),
        ShiftType.AFTERNOON to ShiftDetails(ShiftType.AFTERNOON, "14:45", "22:45"),
        ShiftType.NIGHT to ShiftDetails(ShiftType.NIGHT, "22:45", "06:45")
    )
}