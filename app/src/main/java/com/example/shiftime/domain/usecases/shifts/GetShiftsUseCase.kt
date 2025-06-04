package com.example.shiftime.domain.usecases.shifts

import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class GetShiftsUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository
) {

    suspend fun getAllShifts(): Result<List<Shift>> {
        return try {
            val shifts = shiftRepository.getAllShifts().first()
            Result.success(shifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShiftsByWorkWeek(workWeekId: Long): Result<List<Shift>> {
        return try {
            val shifts = shiftRepository.getShiftsByWorkWeekId(workWeekId).first()
            Result.success(shifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShiftsByDay(day: Days): Result<List<Shift>> {
        return try {
            val shifts = shiftRepository.getShiftsByDay(day).first()
            Result.success(shifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getShiftsByDayFlow(day: Days): Flow<List<Shift>> {
        return shiftRepository.getShiftsByDay(day)
    }

    suspend fun getCurrentShift(): Result<Shift?> {
        return try {
            val currentShift = shiftRepository.getCurrentShift().first()
            Result.success(currentShift)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNextShift(): Result<Shift?> {
        return try {
            val nextShift = shiftRepository.getNextShift().first()
            Result.success(nextShift)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodayShifts(): Result<List<Shift>> {
        return try {
            val todayShifts = shiftRepository.getTodayShifts().first()
            Result.success(todayShifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShiftsForDateRange(startDate: LocalDate, endDate: LocalDate): Result<List<Shift>> {
        return try {
            val shifts = shiftRepository.getShiftsForDateRange(startDate, endDate).first()
            Result.success(shifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getShiftById(shiftId: Long): Result<Shift?> {
        return try {
            val shift = shiftRepository.getShiftById(shiftId)
            if(shift.isSuccess){
                Result.success(shift.getOrNull())
            } else {
                Result.failure(shift.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}