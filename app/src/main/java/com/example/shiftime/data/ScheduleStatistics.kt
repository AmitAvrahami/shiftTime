package com.example.shiftime.data

import com.example.shiftime.models.ShiftSchedule

class ScheduleStatistics(
    val totalShifts: Int,
    val filledShifts: Int,
    val unfilledShifts: Int,
    val fillPercentage: Int,
    val employeeShiftCounts: Map<String, Int>,
    val maxShiftsEmployee: Pair<String, Int>?,
    val minShiftsEmployee: Pair<String, Int>?
) {
    fun printSummary() {
        println("📊 סיכום סידור עבודה:")
        println("סה\"כ משמרות: $totalShifts")
        println("משמרות מולאו: $filledShifts")
        println("חורים בסידור: $unfilledShifts")
        println("אחוז הצלחה: $fillPercentage%")

        println("\n👥 חלוקת משמרות לעובדים:")
        employeeShiftCounts.entries.sortedByDescending { it.value }.forEach { (name, count) ->
            println("$name – $count משמרות")
        }

        println("\n🏆 הכי הרבה משמרות: ${maxShiftsEmployee?.first} (${maxShiftsEmployee?.second})")
        println("😴 הכי מעט משמרות: ${minShiftsEmployee?.first} (${minShiftsEmployee?.second})")
    }

    companion object {
        fun fromSchedule(schedule: ShiftSchedule): ScheduleStatistics {
            val totalShifts = schedule.shifts.size
            val unfilledShifts = schedule.shifts.count { it.employeesId.size < it.employeesRequired }
            val filledShifts = totalShifts - unfilledShifts
            val fillPercentage = if (totalShifts == 0) 0 else (filledShifts * 100) / totalShifts

            val employeeShiftCounts = schedule.employees.associate { employee ->
                val fullName = "${employee.firstName} ${employee.lastName}"
                fullName to employee.shifts.size
            }

            val max = employeeShiftCounts.maxByOrNull { it.value }?.toPair()
            val min = employeeShiftCounts.minByOrNull { it.value }?.toPair()

            return ScheduleStatistics(
                totalShifts = totalShifts,
                filledShifts = filledShifts,
                unfilledShifts = unfilledShifts,
                fillPercentage = fillPercentage,
                employeeShiftCounts = employeeShiftCounts,
                maxShiftsEmployee = max,
                minShiftsEmployee = min
            )
        }
    }
}