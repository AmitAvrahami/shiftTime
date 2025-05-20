//package com.example.shiftime.logic.engine
//
//import com.example.shiftime.logic.rules.ShiftRule
//import com.example.shiftime.domain.model.Employee
//import com.example.shiftime.domain.model.Shift
//
//class RuleEngine(private val rules: List<ShiftRule>) {
//    fun isEmployeeValid(employee: Employee, shift: Shift): Boolean {
//        return rules.all { it.isValid(employee, shift) }
//    }
//
//    fun debugEmployeeValidation(employee: Employee, shift: Shift): List<String> {
//        return rules.filterNot { it.isValid(employee, shift) }
//            .map { "❌ ${it.name} נכשל" }
//    }
//}