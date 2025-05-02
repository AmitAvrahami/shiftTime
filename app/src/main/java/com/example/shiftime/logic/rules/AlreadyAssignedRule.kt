package com.example.shiftime.logic.rules

import com.example.shiftime.models.Employee
import com.example.shiftime.models.Shift

class AlreadyAssignedRule : ShiftRule {
    override val name = "AlreadyAssignedRule"
    override fun isValid(employee: Employee, shift: Shift): Boolean {
        return !shift.employeesId.contains(employee.id)
    }
}