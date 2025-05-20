package com.example.shiftime.domain.usecases.assignments

import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import com.example.shiftime.domain.repository.ShiftRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class AssignEmployeeToShiftUseCase @Inject constructor(
    private val shiftAssignmentRepository: ShiftAssignmentRepository,
    private val shiftRepository: ShiftRepository
) {
    suspend operator fun invoke(employeeId: Long, shiftId: Long, note: String? = null): Result<Unit> {
        val shiftResult = try {
            shiftRepository.getShiftById(shiftId)
        } catch (e: Exception) {
            return Result.failure(Exception("Error getting shift data: ${e.message}"))
        }

        if (shiftResult.isFailure) {
            return Result.failure(
                shiftResult.exceptionOrNull() ?: Exception("Shift not found")
            )
        }

        val shift = shiftResult.getOrNull()
            ?: return Result.failure(Exception("Shift data is null"))

        val currentAssignments = shiftAssignmentRepository.getAssignmentCountForShift(shiftId)
        if (currentAssignments >= shift.employeesRequired) {
            return Result.failure(Exception("Shift is already fully staffed"))
        }

        return shiftAssignmentRepository.assignEmployeeToShift(employeeId, shiftId, note)
    }
}