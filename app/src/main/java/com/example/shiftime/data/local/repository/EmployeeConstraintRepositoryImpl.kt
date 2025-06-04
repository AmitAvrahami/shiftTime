package com.example.shiftime.data.local.repository

import com.example.shiftime.data.local.dao.EmployeeConstraintDao
import com.example.shiftime.data.local.dao.ShiftDao
import com.example.shiftime.data.local.entity.EmployeeConstraintEntity
import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.data.local.mapper.toEntity
import com.example.shiftime.domain.model.EmployeeConstraint
import com.example.shiftime.domain.model.ShiftWithConstraint
import com.example.shiftime.domain.repository.EmployeeConstraintRepository
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.find
import kotlin.collections.map

class EmployeeConstraintRepositoryImpl @Inject constructor(
    private val employeeConstraintDao: EmployeeConstraintDao,
    private val shiftDao: ShiftDao
) : EmployeeConstraintRepository {

    override suspend fun saveConstraint(constraint: EmployeeConstraint) {
        employeeConstraintDao.insertConstraint(constraint.toEntity())
    }

    override suspend fun deleteConstraint(employeeId: Long, shiftId: Long) {
        employeeConstraintDao.deleteConstraint(employeeId, shiftId)
    }

    override fun getConstraintsByEmployee(employeeId: Long): Flow<List<EmployeeConstraint>> {
        return employeeConstraintDao.getConstraintsByEmployeeId(employeeId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getConstraintsByEmployeeAndWorkWeek(
        employeeId: Long,
        workWeekId: Long
    ): Flow<List<EmployeeConstraint>> {
        return employeeConstraintDao.getConstraintsByEmployeeAndWorkWeek(employeeId, workWeekId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getConstraintsByEmployeeAndDay(
        employeeId: Long,
        day: Days,
        workWeekId: Long
    ): Flow<List<EmployeeConstraint>> {
        return employeeConstraintDao.getConstraintsByEmployeeAndDay(employeeId, day, workWeekId)
            .map { entities -> entities.map { it.toDomain() } }
    }

    override fun getShiftsWithConstraintsByDay(
        employeeId: Long,
        day: Days,
        workWeekId: Long
    ): Flow<List<ShiftWithConstraint>> {
        return flow {
            val shifts = shiftDao.getShiftsByDayAndWorkWeek(day, workWeekId).first()
            val constraints = employeeConstraintDao.getConstraintsByEmployeeAndDay(employeeId, day, workWeekId).first()

            val shiftsWithConstraints = shifts.map { shiftEntity ->
                val constraint = constraints.find { it.shiftId == shiftEntity.id }
                ShiftWithConstraint(
                    shift = shiftEntity.toDomain(),
                    constraint = constraint?.toDomain()
                )
            }

            emit(shiftsWithConstraints)
        }
    }

    override fun getConstraintsByWorkWeekId(workWeekId: Long): Flow<List<EmployeeConstraintEntity>> = employeeConstraintDao.getConstraintsByWorkWeekId(workWeekId)
}