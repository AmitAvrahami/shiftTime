package com.example.shiftime.algorithm

import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.domain.usecases.shifts.GetShiftsUseCase
import com.example.shiftime.utils.enums.AssignmentStatus
import javax.inject.Inject

interface SchedulingRule {
    fun canAssign(employee: Employee, shift: Shift, currentAssignments: List<ShiftAssignment>): Boolean
}

sealed class SchedulingRules(){
    abstract fun canAssign(employee: Employee, shift: Shift, currentAssignments: List<ShiftAssignment>, allShifts: List<Shift> ): Boolean
    abstract fun getRuleName(): String
    abstract fun getViolationReason(): String

    class NoMultipleShiftsPerDayRule () : SchedulingRules() {
        override fun canAssign(
            employee: Employee,
            shift: Shift,
            currentAssignments: List<ShiftAssignment>,
            allShifts: List<Shift>
        ): Boolean {

            println("🔍 בודק כלל: אסור 2 משמרות ביום עבור ${employee.firstName}")

            val employeeAssignments = currentAssignments.filter { assignment ->
                assignment.employeeId == employee.id &&
                        assignment.status == AssignmentStatus.ASSIGNED  // רק שיבוצים פעילים
            }

            val employeeShifts = employeeAssignments.mapNotNull { assignment ->
                allShifts.find { it.id == assignment.shiftId }
            }

            val hasShiftOnSameDay = employeeShifts.any { employeeShift ->
                employeeShift.shiftDay == shift.shiftDay
            }

            if (hasShiftOnSameDay) {
                println("   ❌ העובד כבר משובץ ליום ${shift.shiftDay.label}")
                val existingShift = employeeShifts.find {
                    it.shiftDay == shift.shiftDay
                }
                println("   📋 משמרת קיימת: ${existingShift?.shiftType?.label}")
                println("   🚫 לא ניתן להוסיף: ${shift.shiftType.label}")

                return false
            }
            println("   ✅ העובד פנוי ביום ${shift.shiftDay.label}")
            return true
        }

        override fun getRuleName() = "אסור מספר משמרות באותו יום"
        override fun getViolationReason() = "העובד כבר משובץ למשמרת באותו יום"
    }
}