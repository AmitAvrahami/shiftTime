package com.example.shiftime.models

import com.example.shiftime.data.SchedulingStrategy
import com.example.shiftime.data.ScoringWeights
import com.example.shiftime.isEmployeeRest
import kotlin.math.absoluteValue

class ShiftSchedule(
    val shifts: List<Shift>,
    val employees: List<Employee>
) {
    val strategy = SchedulingStrategy.MAXIMUM_BALANCE
    val weights = getWeightsForStrategy(strategy)
    override fun toString(): String {
        val builder = StringBuilder()

        val daysOfWeek = listOf(
            Days.SUNDAY, Days.MONDAY, Days.TUESDAY,
            Days.WEDNESDAY, Days.THURSDAY, Days.FRIDAY, Days.SATURDAY
        )

        val shiftTypes = listOf(
            ShiftType.MORNING, ShiftType.AFTERNOON, ShiftType.NIGHT
        )

        val dayNames = listOf("ראשון", "שני", "שלישי", "רביעי", "חמישי", "שישי", "שבת")

        builder.append("משמרת   |")
        for (dayName in dayNames) {
            builder.append(String.format(" %-15s |", dayName))
        }
        builder.appendLine()
        builder.appendLine("-".repeat(150))

        for (shiftType in shiftTypes) {
            val shiftTypeName = when (shiftType) {
                ShiftType.MORNING -> "בוקר"
                ShiftType.AFTERNOON -> "צהריים"
                ShiftType.NIGHT -> "לילה"
            }
            builder.append(String.format("%-9s|", shiftTypeName))

            for (day in daysOfWeek) {
                val relevantShift = shifts.find { it.shiftDay == day && it.shiftType == shiftType }

                if (relevantShift != null && relevantShift.employeesId.isNotEmpty()) {
                    val assignedEmployees = relevantShift.employeesId.joinToString(", ") { id ->
                        employees.find { it.id == id }?.firstName ?: "לא ידוע"
                    }
                    builder.append(String.format(" %-15s |", assignedEmployees))
                } else {
                    builder.append(String.format(" %-15s |", ""))
                }
            }
            builder.appendLine()
        }

        return builder.toString()
    }

    fun getWeightsForStrategy(strategy: SchedulingStrategy): ScoringWeights {
        return when (strategy) {
            SchedulingStrategy.MAXIMUM_BALANCE -> ScoringWeights(
                shiftCountWeight = 5.0,
                workHoursWeight = 3.0,
                restPenaltyWeight = 100.0,
                consecutiveNightPenalty = 50.0,
                maxShiftDistanceWeight = 2.0,
            )
            SchedulingStrategy.MINIMUM_HOLES -> ScoringWeights(
                shiftCountWeight = 1.0,
                workHoursWeight = 1.0,
                restPenaltyWeight = 50.0,
                consecutiveNightPenalty = 30.0,
                maxShiftDistanceWeight = 5.0,
            )
//            SchedulingStrategy.RESPECT_PREFERENCES -> ScoringWeights(
//                shiftCountWeight = 2.0,
//                workHoursWeight = 2.0,
//                restPenaltyWeight = 100.0,
//                consecutiveNightPenalty = 20.0,
//                maxShiftDistanceWeight = 2.0,
//                preferenceWeight = 10.0
//            )
        }
    }

    fun addEmployeeToShift(employeeId: String, shift: Shift) {
        this.shifts.find { it.id == shift.id }?.employeesId?.add(employeeId)
        this.employees.find { it.id == employeeId }?.shifts?.add(shift)
    }

    fun removeEmployeeFromShift(employeeId: String, shift: Shift) {
        this.shifts.find { it.id == shift.id }?.employeesId?.remove(employeeId)
        this.employees.find { it.id == employeeId }?.shifts?.remove(shift)
    }

    fun sortEmployeesForShift(schedule: ShiftSchedule, shift: Shift): List<Employee> {
        val idealMaxShifts = schedule.shifts.size / schedule.employees.size

        return schedule.employees.sortedBy { employee ->
            calculateEmployeeScore(employee, shift, idealMaxShifts,weights)
        }
    }

    fun calculateEmployeeScore(
        employee: Employee,
        shift: Shift,
        idealMaxShifts: Int,
        weights: ScoringWeights,
        minRestHours: Double = 8.0
    ): Double {
        var score = 0.0

        if (employee.shifts.size > idealMaxShifts) {
            score += (employee.shifts.size - idealMaxShifts) * weights.shiftCountWeight
        } else {
            score += employee.shifts.size * weights.shiftCountWeight
        }

        score += employee.workHours.sum() * weights.workHoursWeight

        if (!isEmployeeRest(employee, shift, minRestHours)) {
            score += weights.restPenaltyWeight
        }

        val lastShift = employee.shifts.lastOrNull()
        if (lastShift != null && lastShift.shiftType == ShiftType.NIGHT && shift.shiftType == ShiftType.NIGHT) {
            score += weights.consecutiveNightPenalty
        }

        val idealDifference = (employee.maxShifts - employee.shifts.size).absoluteValue
        score -= idealDifference * weights.maxShiftDistanceWeight


        return score
    }

    fun addUnAvailableShift(employeeId: String, shifts: List<Shift>) {
        this.employees.find { it.id == employeeId }?.unavailableShifts?.addAll(shifts)
    }

    fun getAllShiftsByDay(day: Days): List<Shift> {
        return this.shifts.filter { it.shiftDay == day }
    }

    fun scheduleMorningShifts(vararg employeesNames: String){
        val morningShifts = this.shifts.filter { it.shiftType == ShiftType.MORNING }
        val employees = this.employees.filter { it.firstName in employeesNames }
        for (shift in morningShifts) {
            if (shift.employeesId.size <= shift.employeesRequired) {
                val allOtherShiftsByDay = getAllShiftsByDay(shift.shiftDay).filter { it.shiftType != ShiftType.MORNING }
                for (employee in employees) {
                    if (shift.employeesId.size < shift.employeesRequired) {
                        if (!employee.unavailableShifts.contains(shift)) {
                            addEmployeeToShift(employee.id, shift)
                            //addUnAvailableShift(employee.id, allOtherShiftsByDay)
                        }
                        addUnAvailableShift(employee.id, allOtherShiftsByDay)
                    }

                }

            }
            }
    }

    fun allShiftsAssigned(): Boolean {
        return this.shifts.all { it.employeesId.size >= it.employeesRequired }
    }

    fun randomEmployeesList(): List<Employee> {
        return this.employees.shuffled()
    }
}




