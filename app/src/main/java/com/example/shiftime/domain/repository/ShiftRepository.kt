package com.example.shiftime.domain.repository

import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate

interface ShiftRepository {
    suspend fun saveShifts(shifts: List<Shift>): List<Shift>
    suspend fun updateShift(shift: Shift): Shift
    fun getShiftsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Shift>>
    fun getShiftsByDay(day: Days): Flow<List<Shift>>
    fun getAllShifts(): Flow<List<Shift>>
    fun getShiftsByWorkWeekId(workWeekId: Long): Flow<List<Shift>>
}