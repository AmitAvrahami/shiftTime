package com.example.shiftime.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shiftime.data.local.dao.ShiftDao
import com.example.shiftime.data.mapper.toDomain
import com.example.shiftime.data.mapper.toEntity
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class ShiftRepositoryImpl @Inject constructor(
    private val shiftDao: ShiftDao
) : ShiftRepository {
    override suspend fun saveShifts(shifts: List<Shift>): List<Shift> {
        val entities = shifts.map { it.toEntity() }
        val insertedIds = shiftDao.insertShifts(entities)

        // החזרת המשמרות עם ה-IDs החדשים שנוצרו
        return shifts.mapIndexed { index, shift ->
            shift.copy(id = insertedIds[index])
        }
    }

    override suspend fun updateShift(shift: Shift): Shift {
        val entity = shift.toEntity()
        shiftDao.updateShift(entity)
        return shift
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getShiftsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Shift>> {
        val startCalendar = Calendar.getInstance().apply {
            set(startDate.year, startDate.monthValue - 1, startDate.dayOfMonth, 0, 0, 0)
        }

        val endCalendar = Calendar.getInstance().apply {
            set(endDate.year, endDate.monthValue - 1, endDate.dayOfMonth, 23, 59, 59)
        }

        return shiftDao.getShiftsForDateRange(startCalendar.time, endCalendar.time)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getShiftsByDay(day: Days): Flow<List<Shift>> {
        return shiftDao.getShiftsByDay(day.name)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getAllShifts(): Flow<List<Shift>> {
        return shiftDao.getAllShifts()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getShiftsByWorkWeekId(workWeekId: Long): Flow<List<Shift>> {
        return shiftDao.getShiftsByWorkWeekId(workWeekId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getShiftById(id: Long): Result<Shift?> {
        return try {
            val shiftEntity = shiftDao.getShiftById(id)
            Result.success(shiftEntity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}