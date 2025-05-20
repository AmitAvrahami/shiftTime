//package com.example.shiftime.logic.rules
//
//import com.example.shiftime.domain.model.Employee
//import com.example.shiftime.domain.model.Shift
//import kotlin.math.absoluteValue
//
//class RestRule(private val minRestHours: Double) : ShiftRule {
//    override val name = "RestRule"
//    override fun isValid(employee: Employee, shift: Shift): Boolean {
//        val lastEmployeeShift = employee.shifts.maxByOrNull { it.endTime } ?: return true
//        if (employee.shifts.filter { it.shiftDay == shift.shiftDay }.size > 1) return false
//        var restTimeMillis = 0L
//        var restTimeHours = 0.0
//        if(shift.startTime.after(lastEmployeeShift.startTime)) {
//            restTimeMillis = (shift.startTime.time - lastEmployeeShift.endTime.time).absoluteValue
//            restTimeHours = restTimeMillis / (1000.0 * 60 * 60) // מילישניות -> שעות
//        }
//        else{
//            restTimeMillis = (lastEmployeeShift.startTime.time - shift.endTime.time).absoluteValue
//            restTimeHours = restTimeMillis / (1000.0 * 60 * 60) // מילישניות -> שעות
//        }
//        return restTimeHours >= minRestHours
//    }
//}