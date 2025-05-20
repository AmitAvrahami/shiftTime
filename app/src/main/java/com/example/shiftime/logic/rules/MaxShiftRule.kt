//package com.example.shiftime.logic.rules
//
//import com.example.shiftime.domain.model.Employee
//import com.example.shiftime.domain.model.Shift
//
//class MaxShiftRule : ShiftRule {
//    override val name = "MaxShiftRule"
//    override fun isValid(employee: Employee, shift: Shift): Boolean {
//        return employee.shifts.size < employee.maxShifts
//    }
//}