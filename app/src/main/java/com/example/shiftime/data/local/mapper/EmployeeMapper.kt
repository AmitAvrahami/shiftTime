package com.example.shiftime.data.local.mapper

import com.example.shiftime.R
import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel
import com.example.shiftime.utils.enums.Role
import java.util.Date

// EmployeeEntity -> Domain
fun EmployeeEntity.toDomain(
    assignedShiftIds: List<Long> = emptyList(),
    unavailableShiftIds: List<Long> = emptyList()
): Employee {
    return Employee(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        idNumber = this.idNumber,
        email = this.email,
        phoneNumber = this.phoneNumber,
        address = this.address,
        dateOfBirth = Date(this.dateOfBirth),
        maxShifts = this.maxShifts,
        minShifts = this.minShifts,
        totalWorkHoursLimit = this.totalWorkHoursLimit,
        role = Role.valueOf(this.role),
        assignedShiftIds = assignedShiftIds,
        unavailableShiftIds = unavailableShiftIds
    )
}

// Domain -> EmployeeEntity
fun Employee.toEntity(): EmployeeEntity {
    return EmployeeEntity(
        id = this.id,
        firstName = this.firstName,
        lastName = this.lastName,
        idNumber = this.idNumber,
        email = this.email,
        phoneNumber = this.phoneNumber,
        address = this.address,
        dateOfBirth = this.dateOfBirth.time,
        maxShifts = this.maxShifts,
        minShifts = this.minShifts,
        totalWorkHoursLimit = this.totalWorkHoursLimit,
        role = this.role.name
    )
}

// Domain -> EmployeeUiModel
fun Employee.toUiModel(): EmployeeUiModel {
    return EmployeeUiModel(
        id = this.id,
        employeeId = this.idNumber.toString(),
        employeeImage = R.drawable.employee_icon,
        employeeName = "${this.firstName} ${this.lastName}",
        employeeDesignation = this.role.name,
        employeePhone = this.phoneNumber,
        minShifts = this.minShifts,
        maxShifts = this.maxShifts,
        isExpanded = false
    )
}

fun EmployeeUiModel.toDomain(): Employee {
    val nameParts = this.employeeName.split(" ", limit = 2)
    val firstName = nameParts[0]
    val lastName = if (nameParts.size > 1) nameParts[1] else ""

    return Employee(
        id = this.id,
        firstName = firstName,
        lastName = lastName,
        idNumber = this.employeeId,
        email = "",
        phoneNumber = this.employeePhone,
        address = "",
        dateOfBirth = Date(),
        maxShifts = this.maxShifts,
        minShifts = this.minShifts,
        totalWorkHoursLimit = 40.0,
        role = Role.valueOf(this.employeeDesignation),
        assignedShiftIds = emptyList(),
        unavailableShiftIds = emptyList()
    )
}

fun EmployeeUiModel.toEntity(): EmployeeEntity {
    return this.toDomain().toEntity()
}

fun EmployeeEntity.toUiModel(): EmployeeUiModel {
    return this.toDomain().toUiModel()
}