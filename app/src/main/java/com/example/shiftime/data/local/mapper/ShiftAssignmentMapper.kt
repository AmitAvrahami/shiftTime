package com.example.shiftime.data.local.mapper

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.data.local.entity.EmployeeWithShifts
import com.example.shiftime.data.local.entity.ShiftAssignmentEntity
import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.data.local.entity.ShiftWithEmployees
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.EmployeeWithAssignedShifts
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.domain.model.ShiftWithAssignedEmployees
import com.example.shiftime.utils.enums.AssignmentStatus
import java.util.Date

fun ShiftAssignmentEntity.toDomain(): ShiftAssignment {
    return ShiftAssignment(
        employeeId = employeeId,
        shiftId = shiftId,
        assignedAt = Date(assignedAt),
        status = AssignmentStatus.valueOf(status),
        note = note
    )
}

fun ShiftAssignment.toEntity(): ShiftAssignmentEntity {
    return ShiftAssignmentEntity(
        employeeId = employeeId,
        shiftId = shiftId,
        assignedAt = assignedAt.time,
        status = status.name,
        note = note
    )
}

fun ShiftWithEmployees.toDomain(employeeMapper: (EmployeeEntity) -> Employee, shiftMapper: (ShiftEntity) -> Shift): ShiftWithAssignedEmployees {
    return ShiftWithAssignedEmployees(
        shift = shiftMapper(shift),
        employees = employees.map { employeeMapper(it) }
    )
}

fun EmployeeWithShifts.toDomain(employeeMapper: (EmployeeEntity) -> Employee, shiftMapper: (ShiftEntity) -> Shift): EmployeeWithAssignedShifts {
    return EmployeeWithAssignedShifts(
        employee = employeeMapper(employee),
        shifts = shifts.map { shiftMapper(it) }
    )
}