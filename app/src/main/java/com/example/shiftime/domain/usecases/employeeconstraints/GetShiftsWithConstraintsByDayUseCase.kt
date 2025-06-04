package com.example.shiftime.domain.usecases.employeeconstraints

import com.example.shiftime.domain.model.ShiftWithConstraint
import com.example.shiftime.domain.repository.EmployeeConstraintRepository
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShiftsWithConstraintsByDayUseCase @Inject constructor(
    private val repository: EmployeeConstraintRepository
) {
    operator fun invoke(employeeId: Long, day: Days, workWeekId: Long): Flow<List<ShiftWithConstraint>> {
        return repository.getShiftsWithConstraintsByDay(employeeId, day, workWeekId)
    }
}
