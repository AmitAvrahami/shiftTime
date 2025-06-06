package com.example.shiftime.algorithm

import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.domain.usecases.schedule.SchedulingData
import com.example.shiftime.utils.enums.AssignmentStatus
import java.util.Date

class BestEffortBacktrackingAlgorithm(
    private val ruleEngine: SchedulingRuleEngine
) {

    private var bestSolutionSoFar: MutableList<ShiftAssignment>? = null
    private var bestScore = -1
    private var iterationCount = 0
    private val maxIterations = 100000

    fun assignShifts(schedulingData: SchedulingData): SchedulingResult {

        println("🚀 מתחיל Best-Effort Backtracking Algorithm")

        bestSolutionSoFar = null
        bestScore = -1
        iterationCount = 0

        val unfilledShifts = schedulingData.getUnfilledShifts()
        val availableEmployees = schedulingData.getAvailableEmployees()
        val allShifts = schedulingData.shifts
        val existingAssignments = schedulingData.existingAssignments.toMutableList()

        println("📊 נתונים לשיבוץ:")
        println("   משמרות למילוי: ${unfilledShifts.size}")
        println("   עובדים זמינים: ${availableEmployees.size}")

        if (unfilledShifts.isEmpty()) {
            return SchedulingResult.Perfect(
                message = "🎉 כל המשמרות כבר מאוישות!",
                assignments = emptyList(),
                statistics = createEmptyStatistics(),
                holes = emptyList()
            )
        }

        if (availableEmployees.isEmpty()) {
            return SchedulingResult.Failed("❌ אין עובדים זמינים במערכת")
        }

        val currentAssignments = mutableListOf<ShiftAssignment>()
        println("\n🔍 מתחיל תהליך הbacktracking...")

        backtrack(
            unfilledShifts = unfilledShifts,
            availableEmployees = availableEmployees,
            allShifts = allShifts,
            existingAssignments = existingAssignments,
            currentAssignments = currentAssignments,
            shiftIndex = 0
        )

        return createFinalResult(unfilledShifts, bestSolutionSoFar)
    }

    /**
     * הפונקציה הרקורסיבית - הלב של הbacktracking
     */
    private fun backtrack(
        unfilledShifts: List<Shift>,
        availableEmployees: List<Employee>,
        allShifts: List<Shift>,
        existingAssignments: List<ShiftAssignment>,
        currentAssignments: MutableList<ShiftAssignment>,
        shiftIndex: Int
    ): Boolean {
        iterationCount++

        if (iterationCount > maxIterations) {
            println("⏰ עצירה - הגעתי למקסימום ניסיונות ($maxIterations)")
            return false
        }

        if (shiftIndex >= unfilledShifts.size) {
            val currentScore = currentAssignments.size
            println("🎯 הגעתי לסוף! ציון נוכחי: $currentScore")
            if (currentScore > bestScore) {
                bestScore = currentScore
                bestSolutionSoFar = currentAssignments.toMutableList()
                println("🏆 פתרון חדש וטוב יותר! שובצו ${currentScore} משמרות")
                if (currentScore == unfilledShifts.size) {
                    println("🎉 פתרון מושלם נמצא!")
                    return true
                }
            }
            return false
        }

        val currentShift = unfilledShifts[shiftIndex]
        val depth = shiftIndex
        val indent = "  ".repeat(depth)

        println("${indent}🔍 מעבד משמרת ${shiftIndex + 1}/${unfilledShifts.size}: ${currentShift.shiftDay.label} ${currentShift.shiftType.label}")
        val preferredEmployees = ruleEngine.getPreferredEmployees(
            shift = currentShift,
            availableEmployees = availableEmployees,
            currentAssignments = existingAssignments + currentAssignments,
            allShifts = allShifts
        )
        println("   ⭐ עובדים מועדפים: ${preferredEmployees.size}")
        preferredEmployees.forEach { emp ->
            println("      • ${emp.firstName} (מועדף)")
        }

        if (tryEmployeesList(
                preferredEmployees,
                "מועדפים",
                currentShift,
                currentAssignments,
                unfilledShifts,
                availableEmployees,
                allShifts,
                existingAssignments,
                shiftIndex
            )
        ) {
            return true
        }


        val nonPreferredEmployees = ruleEngine.getPossibleEmployees(
            shift = currentShift,
            availableEmployees = availableEmployees,
            currentAssignments = existingAssignments + currentAssignments,
            allShifts = allShifts
        )
        println("   ⚠️ עובדים לא מועדפים: ${nonPreferredEmployees.size}")
        nonPreferredEmployees.forEach { emp ->
            println("      • ${emp.firstName} (לא מועדף)")
        }
        if (tryEmployeesList(
                nonPreferredEmployees,
                "לא מועדפים",
                currentShift,
                currentAssignments,
                unfilledShifts,
                availableEmployees,
                allShifts,
                existingAssignments,
                shiftIndex
            )
        ) {
            return true
        }

        println("${indent}   🕳️ מנסה לדלג על המשמרת")

        val foundSolutionWithHole = backtrack(
            unfilledShifts = unfilledShifts,
            availableEmployees = availableEmployees,
            allShifts = allShifts,
            existingAssignments = existingAssignments,
            currentAssignments = currentAssignments,
            shiftIndex = shiftIndex + 1
        )

        if (foundSolutionWithHole) {
            return true
        }

        println("${indent}❌ לא מצאתי פתרון למשמרת ${currentShift.shiftDay.label} ${currentShift.shiftType.label}")
        return false
    }

    /**
     * פונקציה עזר לניסיון רשימת עובדים
     */
    private fun tryEmployeesList(
        employees: List<Employee>,
        listType: String,
        shift: Shift,
        currentAssignments: MutableList<ShiftAssignment>,
        unfilledShifts: List<Shift>,
        availableEmployees: List<Employee>,
        allShifts: List<Shift>,
        existingAssignments: List<ShiftAssignment>,
        shiftIndex: Int
    ): Boolean {

        for (employee in employees) {

            println("      🧪 מנסה עובד $listType: ${employee.firstName}")

            val assignment = ShiftAssignment(
                employeeId = employee.id,
                shiftId = shift.id,
                assignedAt = Date(System.currentTimeMillis()),
                status = AssignmentStatus.ASSIGNED,
                note = "Backtracking ($listType)"
            )

            currentAssignments.add(assignment)

            val foundSolution = backtrack(
                unfilledShifts = unfilledShifts,
                availableEmployees = availableEmployees,
                allShifts = allShifts,
                existingAssignments = existingAssignments,
                currentAssignments = currentAssignments,
                shiftIndex = shiftIndex + 1
            )

            if (foundSolution) {
                return true
            }

            // BACKTRACK
            currentAssignments.removeAt(currentAssignments.size - 1)
            println("      🔙 מבטל שיבוץ של ${employee.firstName}")
        }

        return false
    }

    /**
     * יצירת תוצאה סופית
     */
    private fun createFinalResult(
        unfilledShifts: List<Shift>,
        bestSolution: List<ShiftAssignment>?
    ): SchedulingResult {

        if (bestSolution == null) {
            return SchedulingResult.Failed("❌ לא הצלחתי למצוא אף פתרון")
        }

        val totalShifts = unfilledShifts.size
        val assignedShifts = bestSolution.size
        val holes = totalShifts - assignedShifts

        val statistics = ScheduleStatistics(
            totalShiftsToFill = totalShifts,
            shiftsSuccessfullyFilled = assignedShifts,
            totalEmployeesInvolved = bestSolution.map { it.employeeId }.distinct().size,
            remainingUnfilledShifts = holes,
            algorithmScore = if (totalShifts > 0) (assignedShifts.toDouble() / totalShifts) * 100 else 100.0,
            executionTimeMs = 0L // נמדוד אחר כך
        )

        return if (holes == 0) {
            SchedulingResult.Perfect(
                message = "🎉 פתרון מושלם! כל ${totalShifts} המשמרות מולאו ב-$iterationCount ניסיונות",
                assignments = bestSolution,
                statistics = statistics,
                holes = emptyList()
            )
        } else {
            val unassignedShifts = findUnassignedShifts(unfilledShifts, bestSolution)

            SchedulingResult.BestEffort(
                message = "✅ הפתרון הטוב ביותר: ${assignedShifts}/${totalShifts} משמרות ב-$iterationCount ניסיונות",
                assignments = bestSolution,
                statistics = statistics,
                holes = unassignedShifts,
                holesCount = holes
            )
        }
    }

    /**
     * מוצא משמרות שלא שובצו
     */
    private fun findUnassignedShifts(
        allShifts: List<Shift>,
        assignments: List<ShiftAssignment>
    ): List<ShiftHole> {

        val assignedShiftIds = assignments.map { it.shiftId }.toSet()

        return allShifts
            .filter { it.id !in assignedShiftIds }
            .map { shift ->
                ShiftHole(
                    shift = shift,
                    reason = "לא נמצא עובד מתאים לפי הכללים או שהאלגוריתם בחר לא למלא",
                    suggestedActions = listOf(
                        "צור קשר עם עובדים באופן אישי",
                        "בדוק אפשרות לגמישות בכללי השיבוץ",
                        "שקול הוספת עובדים זמניים",
                        "בדוק אפשרות לשנות סוג המשמרת"
                    )
                )
            }
    }

    /**
     * יצירת סטטיסטיקות ריקות למקרה שאין מה לשבץ
     */
    private fun createEmptyStatistics(): ScheduleStatistics {
        return ScheduleStatistics(
            totalShiftsToFill = 0,
            shiftsSuccessfullyFilled = 0,
            totalEmployeesInvolved = 0,
            remainingUnfilledShifts = 0,
            algorithmScore = 100.0,
            executionTimeMs = 0L
        )
    }
}


sealed class SchedulingResult {

    data class Perfect(
        val message: String,
        val assignments: List<ShiftAssignment>,
        val statistics: ScheduleStatistics,
        val holes: List<ShiftHole>
    ) : SchedulingResult()

    data class BestEffort(
        val message: String,
        val assignments: List<ShiftAssignment>,
        val statistics: ScheduleStatistics,
        val holes: List<ShiftHole>,
        val holesCount: Int
    ) : SchedulingResult()

    data class Failed(
        val message: String
    ) : SchedulingResult()
}

/**
 * מידע על "חור" במשמרת
 */
data class ShiftHole(
    val shift: Shift,
    val reason: String,
    val suggestedActions: List<String>
)

/**
 * סטטיסטיקות על תהליך השיבוץ
 */
data class ScheduleStatistics(
    val totalShiftsToFill: Int,
    val shiftsSuccessfullyFilled: Int,
    val totalEmployeesInvolved: Int,
    val remainingUnfilledShifts: Int,
    val algorithmScore: Double,
    val executionTimeMs: Long
)