//package com.example.shiftime.logic.rules
//
//import com.example.shiftime.domain.model.Employee
//import com.example.shiftime.domain.model.Shift
//
//class AvailabilityRule : ShiftRule {
//    override val name = "AvailabilityRule"
//    override fun isValid(employee: Employee, shift: Shift): Boolean {
//        return !employee.unavailableShifts.contains(shift)
//    }
//}
