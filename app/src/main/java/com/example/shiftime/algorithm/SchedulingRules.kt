package com.example.shiftime.algorithm

import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.EmployeeConstraint
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.utils.enums.AssignmentStatus
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType


sealed class SchedulingRules() {
    abstract fun canAssign(
        employee: Employee,
        shift: Shift,
        currentAssignments: List<ShiftAssignment>,
        allShifts: List<Shift>
    ): Boolean

    abstract fun getRuleName(): String
    abstract fun getViolationReason(): String

    class NoMultipleShiftsPerDayRule() : SchedulingRules() {
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

    class MaxShiftsPerEmployeeRule() : SchedulingRules() {
        override fun canAssign(
            employee: Employee,
            shift: Shift,
            currentAssignments: List<ShiftAssignment>,
            allShifts: List<Shift>
        ): Boolean {

            println("🔍 בודק כלל: מכסה מקסימלית עבור ${employee.firstName}")

            val employeeCurrentShifts = currentAssignments.count { assignment ->
                assignment.employeeId == employee.id &&
                        assignment.status == AssignmentStatus.ASSIGNED
            }

            println("   📊 משמרות נוכחיות: $employeeCurrentShifts")
            println("   📈 מכסה מקסימלית: ${employee.maxShifts}")

            val canTakeMore = employeeCurrentShifts < employee.maxShifts

            if (!canTakeMore) {
                println("   ❌ העובד הגיע למכסה המקסימלית!")
                println("   🔢 ${employeeCurrentShifts}/${employee.maxShifts} משמרות")
                return false
            }

            val remainingCapacity = employee.maxShifts - employeeCurrentShifts
            println("   ✅ יכול לקבל עוד $remainingCapacity משמרות")

            return true
        }

        override fun getRuleName() = "מכסה מקסימלית לעובד"
        override fun getViolationReason() = "העובד הגיע למכסת המשמרות המקסימלית שלו"
    }

    class EmployeeConstraintsRule(private val constraints: List<EmployeeConstraint>) : SchedulingRules() {
        override fun canAssign(
            employee: Employee,
            shift: Shift,
            currentAssignments: List<ShiftAssignment>,
            allShifts: List<Shift>
        ): Boolean {

            println("🔍 בודק כלל: אילוצי עובד עבור ${employee.firstName}")

            val relevantConstraint = constraints.find { constraint ->
                constraint.employeeId == employee.id &&
                        constraint.shiftId == shift.id
            }

            if (relevantConstraint == null) {
                println("   📝 לא נמצא אילוץ ספציפי למשמרת הזו")
                println("   ✅ default: העובד יכול לעבוד")
                return true
            }

            println("   📋 נמצא אילוץ: canWork = ${relevantConstraint.canWork}")

            if (relevantConstraint.canWork) {
                println("   ✅ העובד ציין שהוא יכול לעבוד במשמרת הזו")
                return true
            } else {
                println("   ❌ העובד ציין שהוא לא יכול לעבוד במשמרת הזו")

                relevantConstraint.comment?.let { comment ->
                    println("   💬 הערת העובד: $comment")
                }

                return false
            }
        }

        override fun getRuleName() = "אילוצי עובד"
        override fun getViolationReason() = "העובד ציין שהוא לא יכול לעבוד במשמרת זו"
    }

    class AvoidConsecutive8to8Rule() : SchedulingRules() {
        override fun canAssign(
            employee: Employee,
            shift: Shift,
            currentAssignments: List<ShiftAssignment>,
            allShifts: List<Shift>
        ): Boolean {
            val employeeAssignments = currentAssignments.filter { assignment ->
                assignment.employeeId == employee.id &&
                        assignment.status == AssignmentStatus.ASSIGNED
            }

            val employeeShifts = employeeAssignments.mapNotNull { assignment ->
                allShifts.find { it.id == assignment.shiftId }
            }

            println("   📋 העובד יש לו ${employeeShifts.size} משמרות כרגע")

            when (shift.shiftType) {
                ShiftType.MORNING -> {
                    // אם רוצים לשבץ לבוקר - בדוק שלא עבד צהריים אתמול
                    return checkNotWorkedYesterday(
                        employeeShifts = employeeShifts,
                        newShiftDay = shift.shiftDay,
                        conflictShiftType = ShiftType.AFTERNOON,
                        reason = "צהריים אתמול ← בוקר היום"
                    )
                }

                ShiftType.AFTERNOON -> {
                    // אם רוצים לשבץ לצהריים - בדוק שלא עבד לילה אתמול
                    return checkNotWorkedYesterday(
                        employeeShifts = employeeShifts,
                        newShiftDay = shift.shiftDay,
                        conflictShiftType = ShiftType.NIGHT,
                        reason = "לילה אתמול ← צהריים היום"
                    )
                }

                ShiftType.NIGHT -> {
                    // משמרת לילה בדרך כלל בסדר, אלא אם כן עבד בוקר או צהריים היום
                    // אבל זה כבר מטופל בכלל "אסור 2 משמרות באותו יום"
                    println("   ✅ משמרת לילה - לא בעייתית מבחינת 8-8")
                    return true
                }
            }

        }

        /**
         * בודק שהעובד לא עבד סוג משמרת מסוים אתמול
         */
        private fun checkNotWorkedYesterday(
            employeeShifts: List<Shift>,
            newShiftDay: Days,
            conflictShiftType: ShiftType,
            reason: String
        ): Boolean {

            val previousDay = getPreviousDay(newShiftDay)
            println("   📅 בודק שלא עבד ${conflictShiftType.label} ב-${previousDay.label}")

            // חפש אם יש לו משמרת מהסוג הבעייתי ביום הקודם
            val hasConflictingShift = employeeShifts.any { employeeShift ->
                employeeShift.shiftDay == previousDay &&
                        employeeShift.shiftType == conflictShiftType
            }

            if (hasConflictingShift) {
                println("   ❌ נמצאה משמרת בעייתית!")
                println("   🚫 מצב 8-8: $reason")
                println("   ⏰ זה לא נותן מספיק זמן מנוחה לעובד")
                return false
            }

            println("   ✅ לא נמצאה משמרת בעייתית ב-${previousDay.label}")
            return true
        }

        private fun getPreviousDay(day: Days): Days {
            return when (day) {
                Days.SUNDAY -> Days.SATURDAY
                Days.MONDAY -> Days.SUNDAY
                Days.TUESDAY -> Days.MONDAY
                Days.WEDNESDAY -> Days.TUESDAY
                Days.THURSDAY -> Days.WEDNESDAY
                Days.FRIDAY -> Days.THURSDAY
                Days.SATURDAY -> Days.FRIDAY
            }
        }

        override fun getRuleName(): String = "הימנעות מ-8-8"

        override fun getViolationReason(): String =
            "לא מספיק זמן מנוחה בין משמרות (פחות מ-8 שעות)"

    }
}