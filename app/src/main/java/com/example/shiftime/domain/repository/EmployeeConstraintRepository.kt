package com.example.shiftime.domain.repository

import com.example.shiftime.data.local.entity.EmployeeConstraintEntity
import com.example.shiftime.domain.model.EmployeeConstraint
import com.example.shiftime.domain.model.ShiftWithConstraint
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow

interface EmployeeConstraintRepository {
    suspend fun saveConstraint(constraint: EmployeeConstraint)
    suspend fun deleteConstraint(employeeId: Long, shiftId: Long)
    fun getConstraintsByEmployee(employeeId: Long): Flow<List<EmployeeConstraint>>
    fun getConstraintsByEmployeeAndWorkWeek(employeeId: Long, workWeekId: Long): Flow<List<EmployeeConstraint>>
    fun getConstraintsByEmployeeAndDay(employeeId: Long, day: Days, workWeekId: Long): Flow<List<EmployeeConstraint>>
    fun getShiftsWithConstraintsByDay(employeeId: Long, day: Days, workWeekId: Long): Flow<List<ShiftWithConstraint>>
    fun getConstraintsByWorkWeekId(workWeekId: Long): Flow<List<EmployeeConstraintEntity>>
}