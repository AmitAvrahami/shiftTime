package com.example.shiftime.domain.usecases.assignments

import com.example.shiftime.domain.model.EmployeeWithShifts
import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmployeeWithAssignedShiftsUseCase @Inject constructor(
    private val shiftAssignmentRepository: ShiftAssignmentRepository
) {
    operator fun invoke(employeeId: Long): Flow<EmployeeWithShifts> {
        return shiftAssignmentRepository.getEmployeeWithAssignedShifts(employeeId)
    }
}