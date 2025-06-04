package com.example.shiftime.domain.usecases.employeeconstraints

import com.example.shiftime.domain.repository.EmployeeConstraintRepository
import javax.inject.Inject

class DeleteEmployeeConstraintUseCase @Inject constructor(
    private val repository: EmployeeConstraintRepository
) {
    suspend operator fun invoke(employeeId: Long, shiftId: Long) {
        repository.deleteConstraint(employeeId, shiftId)
    }
}