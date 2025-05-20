package com.example.shiftime.domain.usecases.assignments

import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import com.example.shiftime.utils.enums.AssignmentStatus
import javax.inject.Inject

class UpdateAssignmentStatusUseCase @Inject constructor(
    private val shiftAssignmentRepository: ShiftAssignmentRepository
) {
    suspend operator fun invoke(employeeId: Long, shiftId: Long, status: AssignmentStatus): Result<Unit> {
        return shiftAssignmentRepository.updateAssignmentStatus(employeeId, shiftId, status)
    }
}