package com.example.shiftime.domain.repository

import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface ShiftRepository {
    suspend fun saveShifts(shifts: List<Shift>): Result<List<Shift>>
    suspend fun updateShift(shift: Shift): Result<Shift>
    fun getShiftsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Shift>>
    fun getShiftsByDay(day: Days): Flow<List<Shift>>
    fun getAllShifts(): Flow<List<Shift>>
    suspend fun getShiftById(id: Long): Result<Shift?>
    suspend fun getTodayShiftsWithEmployees(): Flow<List<ShiftWithEmployees>>
     fun getNextShift(): Flow<Shift?>
    fun getCurrentShift(): Flow<Shift?>
    fun getTodayShifts(): Flow<List<Shift>>
    fun getShiftsByWorkWeekId(workWeekId: Long): Flow<List<Shift>>
    suspend fun createShift(shift: Shift): Result<Shift>
    suspend fun deleteShift(shiftId: Long): Result<Unit>
    fun getShiftWithEmployeesById(shiftId: Long): Flow<ShiftWithEmployees?>
}