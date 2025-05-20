package com.example.shiftime.domain.usecases.assignments

import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import javax.inject.Inject

class RemoveEmployeeFromShiftUseCase @Inject constructor(
    private val shiftAssignmentRepository: ShiftAssignmentRepository
) {
    suspend operator fun invoke(employeeId: Long, shiftId: Long): Result<Unit> {
        return shiftAssignmentRepository.removeEmployeeFromShift(employeeId, shiftId)
    }
}