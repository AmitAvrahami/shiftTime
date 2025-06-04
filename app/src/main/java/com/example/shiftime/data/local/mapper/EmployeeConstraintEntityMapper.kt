package com.example.shiftime.data.local.mapper

import com.example.shiftime.data.local.entity.EmployeeConstraintEntity
import com.example.shiftime.domain.model.EmployeeConstraint
import java.util.Date

fun EmployeeConstraintEntity.toDomain(): EmployeeConstraint {
    return EmployeeConstraint(
        employeeId = employeeId,
        shiftId = shiftId,
        canWork = canWork,
        comment = comment,
        createdAt = Date(createdAt)
    )
}

fun EmployeeConstraint.toEntity(): EmployeeConstraintEntity {
    return EmployeeConstraintEntity(
        employeeId = employeeId,
        shiftId = shiftId,
        canWork = canWork,
        comment = comment,
        createdAt = createdAt.time
    )
}