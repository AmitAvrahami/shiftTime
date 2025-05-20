package com.example.shiftime.data.local.repository

import com.example.shiftime.data.local.dao.ShiftAssignmentDao
import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.data.local.entity.ShiftAssignmentEntity
import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.EmployeeWithAssignedShifts
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.domain.model.ShiftWithAssignedEmployees
import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import com.example.shiftime.utils.enums.AssignmentStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShiftAssignmentRepositoryImpl @Inject constructor(
    private val shiftAssignmentDao: ShiftAssignmentDao,
    private val employeeMapper: Function1<EmployeeEntity, Employee>,
    private val shiftMapper: Function1<ShiftEntity, Shift>
) : ShiftAssignmentRepository {

    override suspend fun assignEmployeeToShift(employeeId: Long, shiftId: Long, note: String?): Result<Unit> {
        return try {
            val assignment = ShiftAssignmentEntity(
                employeeId = employeeId,
                shiftId = shiftId,
                assignedAt = System.currentTimeMillis(),
                status = AssignmentStatus.ASSIGNED.name,
                note = note
            )
            shiftAssignmentDao.insertAssignment(assignment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeEmployeeFromShift(employeeId: Long, shiftId: Long): Result<Unit> {
        return try {
            shiftAssignmentDao.deleteAssignment(employeeId, shiftId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateAssignmentStatus(
        employeeId: Long,
        shiftId: Long,
        status: AssignmentStatus
    ): Result<Unit> {
        return try {
            // אנחנו צריכים קודם לקבל את השיבוץ הקיים ואז לעדכן את הסטטוס שלו
            val currentAssignments = shiftAssignmentDao.getAssignmentsForShift(shiftId).first()
            val assignmentToUpdate = currentAssignments.find { it.employeeId == employeeId }
                ?: return Result.failure(Exception("Assignment not found"))

            val updatedAssignment = assignmentToUpdate.copy(status = status.name)
            shiftAssignmentDao.insertAssignment(updatedAssignment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAssignmentsForShift(shiftId: Long): Flow<List<ShiftAssignment>> {
        return shiftAssignmentDao.getAssignmentsForShift(shiftId)
            .map { assignments -> assignments.map { it.toDomain() } }
    }

    override fun getAssignmentsForEmployee(employeeId: Long): Flow<List<ShiftAssignment>> {
        return shiftAssignmentDao.getAssignmentsForEmployee(employeeId)
            .map { assignments -> assignments.map { it.toDomain() } }
    }

    override fun getShiftWithAssignedEmployees(shiftId: Long): Flow<ShiftWithAssignedEmployees> {
        return shiftAssignmentDao.getShiftWithAssignedEmployees(shiftId)
            .map { it.toDomain(employeeMapper, shiftMapper) }
    }

    override fun getEmployeeWithAssignedShifts(employeeId: Long): Flow<EmployeeWithAssignedShifts> {
        return shiftAssignmentDao.getEmployeeWithAssignedShifts(employeeId)
            .map { it.toDomain(employeeMapper, shiftMapper) }
    }

    override suspend fun getAssignmentCountForShift(shiftId: Long): Int {
        return shiftAssignmentDao.getAssignmentCountForShift(shiftId)
    }
}