package com.example.shiftime.domain.usecases.employees

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.presentation.ui.common.state.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetEmployeesUseCase @Inject constructor(
    private val employeeRepository: EmployeeRepository
) {
     operator fun invoke() : Flow<List<Employee>> {
        try {
            return employeeRepository.getAllEmployees().map { it.map { it.toDomain() } }
            }
        catch (e: Exception) {
            throw e
        }
    }

}