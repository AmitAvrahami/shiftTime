package com.example.shiftime.domain.usecases.assignments

import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShiftWithAssignedEmployeesUseCase @Inject constructor(
    private val shiftAssignmentRepository: ShiftAssignmentRepository
) {
    operator fun invoke(shiftId: Long): Flow<ShiftWithEmployees> {
        return shiftAssignmentRepository.getShiftWithAssignedEmployees(shiftId)
    }
}