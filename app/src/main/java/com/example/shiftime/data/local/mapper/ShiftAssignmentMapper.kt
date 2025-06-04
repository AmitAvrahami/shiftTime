package com.example.shiftime.data.local.mapper

import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.data.local.entity.EmployeeWithShiftsEntity
import com.example.shiftime.data.local.entity.ShiftAssignmentEntity
import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.data.local.entity.ShiftWithEmployeesEntity
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.EmployeeWithShifts
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.domain.model.ShiftWithEmployees
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

fun ShiftWithEmployeesEntity.toDomain(employeeMapper: (EmployeeEntity) -> Employee, shiftMapper: (ShiftEntity) -> Shift): ShiftWithEmployees {
    return ShiftWithEmployees(
        shift = shiftMapper(shift),
        employees = employees.map { employeeMapper(it) }
    )
}

fun EmployeeWithShiftsEntity.toDomain(employeeMapper: (EmployeeEntity) -> Employee, shiftMapper: (ShiftEntity) -> Shift): EmployeeWithShifts {
    return EmployeeWithShifts(
        employee = employeeMapper(employee),
        shifts = shifts.map { shiftMapper(it) }
    )
}

fun ShiftWithEmployees.toEntity(): ShiftWithEmployeesEntity {
    return ShiftWithEmployeesEntity(
        shift = shift.toEntity(),
        employees = employees.map { it.toEntity() }

    )

}