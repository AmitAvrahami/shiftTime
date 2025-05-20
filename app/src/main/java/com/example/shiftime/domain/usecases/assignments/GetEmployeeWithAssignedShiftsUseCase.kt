package com.example.shiftime.domain.usecases.assignments

import com.example.shiftime.domain.model.EmployeeWithAssignedShifts
import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetEmployeeWithAssignedShiftsUseCase @Inject constructor(
    private val shiftAssignmentRepository: ShiftAssignmentRepository
) {
    operator fun invoke(employeeId: Long): Flow<EmployeeWithAssignedShifts> {
        return shiftAssignmentRepository.getEmployeeWithAssignedShifts(employeeId)
    }
}