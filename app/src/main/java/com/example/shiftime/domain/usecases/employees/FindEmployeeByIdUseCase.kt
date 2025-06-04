package com.example.shiftime.domain.usecases.employees

import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.presentation.ui.common.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * Use case for finding an employee by their ID.
 *
 * This class encapsulates the business logic for retrieving an employee's details
 * based on their unique identifier. It interacts with an [EmployeeRepository]
 * to fetch the data and emits the result as a [Flow] of [UiState].
 *
 * @property employeeRepository The repository responsible for accessing employee data.
 */
class FindEmployeeByIdUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository
) {
    operator fun invoke(employeeId: Long): Flow<UiState<Employee>> = flow {
        try {
            emit(UiState.Loading)

            val employeeEntity = employeeRepository.getEmployeeById(employeeId)

            if (employeeEntity != null) {
                val employee = employeeEntity.toDomain()
                emit(UiState.Success(employee))
            } else {
                emit(UiState.Error("לא נמצא עובד עם מזהה $employeeId"))
            }
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "שגיאה לא ידועה בחיפוש העובד"))
        }
    }
}