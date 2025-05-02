package com.example.shiftime.logic.rules

import com.example.shiftime.models.Employee
import com.example.shiftime.models.Shift

class AvailabilityRule : ShiftRule {
    override val name = "AvailabilityRule"
    override fun isValid(employee: Employee, shift: Shift): Boolean {
        return !employee.unavailableShifts.contains(shift)
    }
}
