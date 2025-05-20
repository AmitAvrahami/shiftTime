package com.example.shiftime.domain.usecases.employees

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.presentation.ui.common.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository
) {
     operator fun invoke() : Flow<UiState<List<EmployeeEntity>>> = flow {
        emit(UiState.Loading)
        try {
            val employees = employeeRepository.getAllEmployees()
            emit(UiState.Success(employees))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error"))
        }
    }

}