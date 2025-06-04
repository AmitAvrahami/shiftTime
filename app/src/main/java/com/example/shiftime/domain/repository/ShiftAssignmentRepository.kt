package com.example.shiftime.domain.repository

import com.example.shiftime.data.local.entity.ShiftAssignmentEntity
import com.example.shiftime.domain.model.EmployeeWithShifts
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.utils.enums.AssignmentStatus
import kotlinx.coroutines.flow.Flow

interface ShiftAssignmentRepository {
    suspend fun assignEmployeeToShift(employeeId: Long, shiftId: Long, note: String? = null): Result<Unit>
    suspend fun removeEmployeeFromShift(employeeId: Long, shiftId: Long): Result<Unit>
    suspend fun updateAssignmentStatus(employeeId: Long, shiftId: Long, status: AssignmentStatus): Result<Unit>
    fun getAssignmentsForShift(shiftId: Long): Flow<List<ShiftAssignment>>
    fun getAssignmentsForEmployee(employeeId: Long): Flow<List<ShiftAssignment>>
    fun getShiftWithAssignedEmployees(shiftId: Long): Flow<ShiftWithEmployees>
    fun getEmployeeWithAssignedShifts(employeeId: Long): Flow<EmployeeWithShifts>
    suspend fun getAssignmentCountForShift(shiftId: Long): Int
    fun getAssignmentsByWorkWeekId(workWeekId: Long): Flow<List<ShiftAssignmentEntity>>

}