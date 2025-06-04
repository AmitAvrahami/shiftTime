package com.example.shiftime.domain.repository

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.domain.model.Employee
import kotlinx.coroutines.flow.Flow

interface EmployeeRepository {
    suspend fun insertEmployee(employee: EmployeeEntity)
    suspend fun updateEmployee(employee: EmployeeEntity)
    suspend fun deleteEmployee(employee: EmployeeEntity)
    fun getAllEmployees(): Flow<List<EmployeeEntity>>
    suspend fun getEmployeeById(id: Long): EmployeeEntity?
    fun getEmployeesByIds(employeeIds: List<Long>): Flow<List<EmployeeEntity>>
}