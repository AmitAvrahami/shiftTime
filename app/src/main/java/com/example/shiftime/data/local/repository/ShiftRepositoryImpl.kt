package com.example.shiftime.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shiftime.data.local.dao.ShiftDao
import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.data.local.mapper.toEntity
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.util.Date
import javax.inject.Inject

class ShiftRepositoryImpl @Inject constructor(
    private val shiftDao: ShiftDao
) : ShiftRepository {

    override fun getAllShifts(): Flow<List<Shift>> {
        return shiftDao.getAllShifts()
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getShiftsByWorkWeekId(workWeekId: Long): Flow<List<Shift>> {
        return shiftDao.getShiftsByWorkWeekId(workWeekId)
            .map { entities -> entities.map { it.toDomain() } }
    }


    override fun getShiftsByDay(day: Days): Flow<List<Shift>> {
        return shiftDao.getShiftsByDay(day.name)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getCurrentShift(): Flow<Shift?> {
        return shiftDao.getCurrentShift()
            .map { entity -> entity?.toDomain() }
    }

    override fun getTodayShifts(): Flow<List<Shift>> {
        return shiftDao.getTodayShifts()
            .map { entities -> entities.map { it.toDomain() } }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getShiftsForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<Shift>> {
        val startDateTime = Date.from(startDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant())
        val endDateTime = Date.from(endDate.atTime(23, 59, 59).atZone(java.time.ZoneId.systemDefault()).toInstant())

        return shiftDao.getShiftsForDateRange(startDateTime, endDateTime)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun getShiftById(shiftId: Long): Result<Shift?> {
        return try {
            val shiftEntity = shiftDao.getShiftById(shiftId)
            Result.success(shiftEntity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTodayShiftsWithEmployees(): Flow<List<ShiftWithEmployees>> {
        TODO("Not yet implemented")
    }

    override fun getNextShift(): Flow<Shift?> {
        return shiftDao.getNextShift()
            .map { entity -> entity?.toDomain() }
    }

    override fun getShiftWithEmployeesById(shiftId: Long): Flow<ShiftWithEmployees?> {
        return shiftDao.getTodayShiftsWithEmployees()
            .map { entities ->
                entities.find { it.shift.id == shiftId }?.toDomain(
                    employeeMapper = { it.toDomain()},
                    shiftMapper = {it.toDomain()}
                )
            }
    }

    override suspend fun createShift(shift: Shift): Result<Shift> {
        return try {
            val shiftId = shiftDao.insertShift(shift.toEntity())
            val createdShift = shift.copy(id = shiftId)
            Result.success(createdShift)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateShift(shift: Shift): Result<Shift> {
        return try {
            shiftDao.updateShift(shift.toEntity())
            Result.success(shift)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend  fun deleteShift(shiftId: Long): Result<Unit> {
        return try {
            shiftDao.deleteShiftById(shiftId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveShifts(shifts: List<Shift>): Result<List<Shift>> {
        return try {
            val shiftIds = shiftDao.insertShifts(shifts.map { it.toEntity() })
            val savedShifts = shifts.mapIndexed { index, shift ->
                shift.copy(id = shiftIds[index])
            }
            Result.success(savedShifts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}