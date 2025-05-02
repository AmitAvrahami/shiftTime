package com.example.shiftime.logic.rules

import com.example.shiftime.models.Employee
import com.example.shiftime.models.Shift

interface ShiftRule {
    fun isValid(employee: Employee, shift: Shift): Boolean
    val name: String
}