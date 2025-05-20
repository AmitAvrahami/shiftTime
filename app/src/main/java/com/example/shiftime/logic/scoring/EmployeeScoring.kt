//package com.example.shiftime.logic.scoring
//
//import com.example.shiftime.logic.data.ScoringWeights
//import com.example.shiftime.isEmployeeRest
//import com.example.shiftime.domain.model.Employee
//import com.example.shiftime.domain.model.Shift
//import com.example.shiftime.utils.enums.ShiftType
//import kotlin.math.absoluteValue
//
//object EmployeeScoring{
//
//
//    fun calculateEmployeeScore(
//        employee: Employee,
//        shift: Shift,
//        idealMaxShifts: Int,
//        weights: ScoringWeights,
//        minRestHours: Double = 8.0
//    ): Double {
//        var score = 0.0
//
//        if (employee.shifts.size > idealMaxShifts) {
//            score += (employee.shifts.size - idealMaxShifts) * weights.shiftCountWeight
//        } else {
//            score += employee.shifts.size * weights.shiftCountWeight
//        }
//
//        score += employee.workHours.sum() * weights.workHoursWeight
//
//        if (!isEmployeeRest(employee, shift, minRestHours)) {
//            score += weights.restPenaltyWeight
//        }
//
//        val lastShift = employee.shifts.lastOrNull()
//        if (lastShift != null && lastShift.shiftType == ShiftType.NIGHT && shift.shiftType == ShiftType.NIGHT) {
//            score += weights.consecutiveNightPenalty
//        }
//
//        val idealDifference = (employee.maxShifts - employee.shifts.size).absoluteValue
//        score -= idealDifference * weights.maxShiftDistanceWeight
//
//
//        return score
//    }
//}