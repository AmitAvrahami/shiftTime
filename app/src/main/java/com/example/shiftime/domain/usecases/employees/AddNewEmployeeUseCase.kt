package com.example.shiftime.domain.usecases.employees

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.presentation.ui.common.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AddNewEmployeeUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository
) {
    operator fun invoke(employeeEntity: EmployeeEntity) : Flow<UiState<EmployeeEntity>> = flow {
        emit(UiState.Loading)
        try {
            employeeRepository.insertEmployee(employeeEntity)
            emit(UiState.Success(employeeEntity))
        } catch (e: Exception) {
            emit(UiState.Error(e.message ?: "Unknown error"))
        }
    }
}