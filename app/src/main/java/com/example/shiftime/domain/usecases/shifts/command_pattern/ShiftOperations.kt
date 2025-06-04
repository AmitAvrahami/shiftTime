package com.example.shiftime.domain.usecases.shifts.command_pattern

import com.example.shiftime.domain.model.Shift

sealed class ShiftOperations(){

    data class Create(val shift: Shift) : ShiftOperations()
    data class Update( val shiftId: Long, val updatedShift: Shift) : ShiftOperations()
    data class Delete(val shiftId: Long) : ShiftOperations()
    data class SaveShifts(val shifts: List<Shift>) : ShiftOperations()
}