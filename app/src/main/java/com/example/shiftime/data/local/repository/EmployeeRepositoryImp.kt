package com.example.shiftime.data.local.repository

import com.example.shiftime.data.local.dao.EmployeeDao
import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.repository.EmployeeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EmployeeRepositoryImp @Inject constructor(
    private val employeeDao: EmployeeDao
): EmployeeRepository  {
    override suspend fun insertEmployee(employee: EmployeeEntity)= employeeDao.insertEmployee(employee)

    override suspend fun updateEmployee(employee: EmployeeEntity) = employeeDao.updateEmployee(employee)

    override suspend fun deleteEmployee(employee: EmployeeEntity) = employeeDao.deleteEmployee(employee)

    override fun getAllEmployees(): List<EmployeeEntity> = employeeDao.getAllEmployees()

    override suspend fun getEmployeeById(id: Long): EmployeeEntity? = employeeDao.getEmployeeById(id)
}