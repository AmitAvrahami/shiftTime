package com.example.shiftime.domain.usecases.employees

import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.presentation.ui.common.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

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