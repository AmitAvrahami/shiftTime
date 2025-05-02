package com.example.shiftime.logic.rules

import com.example.shiftime.models.Employee
import com.example.shiftime.models.Shift

class MaxShiftRule : ShiftRule {
    override val name = "MaxShiftRule"
    override fun isValid(employee: Employee, shift: Shift): Boolean {
        return employee.shifts.size < employee.maxShifts
    }
}