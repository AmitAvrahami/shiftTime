package com.example.shiftime.algorithm

import com.example.shiftime.algorithm.SchedulingRules.AvoidConsecutive8to8Rule
import com.example.shiftime.algorithm.SchedulingRules.EmployeeConstraintsRule
import com.example.shiftime.algorithm.SchedulingRules.MaxShiftsPerEmployeeRule
import com.example.shiftime.algorithm.SchedulingRules.NoMultipleShiftsPerDayRule
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.EmployeeConstraint
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment

class SchedulingRuleEngine(
    private val constraints: List<EmployeeConstraint> = emptyList()
) {
    private val mandatoryRules = listOf(
        NoMultipleShiftsPerDayRule(),
        MaxShiftsPerEmployeeRule(),
        EmployeeConstraintsRule(constraints)
    )

    private val preferenceRules = listOf(
        AvoidConsecutive8to8Rule()
    )

    /**
     * השאלה הראשית: האם מותר לשבץ את העובד הזה למשמרת הזו?
     *
     * @param employee - העובד שרוצים לשבץ
     * @param shift - המשמרת שרוצים לשבץ אליה
     * @param currentAssignments - כל השיבוצים שכבר עשינו (כדי לבדוק התנגשויות)
     * @return true אם מותר לשבץ, false אם אסור
     */
    fun canAssignEmployee(
        employee: Employee,
        shift: Shift,
        currentAssignments: List<ShiftAssignment>,
        allShifts: List<Shift>
    ): Boolean {

        println("🔍 בודק האם ${employee.firstName} יכול לעבוד ב-${shift.shiftDay.label} ${shift.shiftType.label}")

        for (rule in mandatoryRules) {
            val canAssign = rule.canAssign(employee, shift, currentAssignments, allShifts)

            if (!canAssign) {
                println("   ❌ כלל נכשל: ${rule.getRuleName()}")
                println("      סיבה: ${rule.getViolationReason()}")
                return false
            } else {
                println("   ✅ כלל עבר: ${rule.getRuleName()}")
            }
        }

        println("   🎯 כל הכללים החובה עברו - השיבוץ מותר!")
        return true
    }

    /**
     * בדיקה מתקדמת: האם השיבוץ גם "מועדף"?
     * (כלומר, עובר גם את כללי ההעדפה)
     */
    fun isPreferredAssignment(
        employee: Employee,
        shift: Shift,
        currentAssignments: List<ShiftAssignment>,
        allShifts: List<Shift>
    ): Boolean {

        if (!canAssignEmployee(employee, shift, currentAssignments, allShifts)) {
            return false
        }

        for (rule in preferenceRules) {
            if (!rule.canAssign(employee, shift, currentAssignments, allShifts)) {
                println("   ⚠️ כלל העדפה נכשל: ${rule.getRuleName()}")
                return false
            }
        }

        println("   🌟 השיבוץ גם מועדף!")
        return true
    }

        /**
     * פונקציה לדיבוג: מחזירה רשימת סיבות למה השיבוץ נכשל
     */
    fun getViolationReasons(
        employee: Employee,
        shift: Shift,
        currentAssignments: List<ShiftAssignment>,
        allShifts: List<Shift>
    ): List<String> {

        val violations = mutableListOf<String>()

        mandatoryRules.forEach { rule ->
            if (!rule.canAssign(employee, shift, currentAssignments,allShifts)) {
                violations.add("${rule.getRuleName()}: ${rule.getViolationReason()}")
            }
        }

        return violations
    }

    /**
     * פונקציה שמחזירה רשימת עובדים "מועדפים" למשמרת
     */
    fun getPreferredEmployees(
        shift: Shift,
        availableEmployees: List<Employee>,
        currentAssignments: List<ShiftAssignment>,
        allShifts: List<Shift>
    ): List<Employee> {

        return availableEmployees.filter { employee ->
            isPreferredAssignment(employee, shift, currentAssignments,allShifts)
        }
    }

    /**
     * פונקציה שמחזירה רשימת עובדים "אפשריים" (לא מועדפים אבל מותרים)
     */
    fun getPossibleEmployees(
        shift: Shift,
        availableEmployees: List<Employee>,
        currentAssignments: List<ShiftAssignment>,
        allShifts: List<Shift>
    ): List<Employee> {

        return availableEmployees.filter { employee ->
            canAssignEmployee(employee, shift, currentAssignments,allShifts) &&
                    !isPreferredAssignment(employee, shift, currentAssignments,allShifts)
        }
    }
}

