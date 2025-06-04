package com.example.shiftime.domain.usecases.schedule

import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.EmployeeConstraint
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftAssignment
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.repository.EmployeeConstraintRepository
import com.example.shiftime.domain.repository.EmployeeRepository
import com.example.shiftime.domain.repository.ShiftAssignmentRepository
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.repository.WorkWeekRepository
import com.example.shiftime.utils.enums.AssignmentStatus
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetSchedulingDataUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val employeeRepository: EmployeeRepository,
    private val employeeConstraintRepository: EmployeeConstraintRepository,
    private val workWeekRepository: WorkWeekRepository,
    private val shiftAssignmentRepository: ShiftAssignmentRepository
){
    suspend operator fun invoke(workWeekId: Long): Result<SchedulingData> {
        return try {
            if (workWeekId <= 0) {
                return Result.failure(Exception("מזהה שבוע עבודה לא תקין: $workWeekId"))
            }

            val workWeek = workWeekRepository.getWorkWeekById(workWeekId)
                ?: return Result.failure(Exception("שבוע עבודה לא נמצא: $workWeekId"))

            val shifts = shiftRepository.getShiftsByWorkWeekId(workWeekId).first()
            if (shifts.isEmpty()) {
                return Result.failure(Exception("לא נמצאו משמרות לשבוע $workWeekId"))
            }

            val employees = employeeRepository.getAllEmployees().first().map { it.toDomain() }
            if (employees.isEmpty()) {
                return Result.failure(Exception("לא נמצאו עובדים במערכת"))
            }

            val positiveConstraints = employeeConstraintRepository.getConstraintsByWorkWeekId(workWeekId).first()
                .map { it.toDomain() }

            val existingAssignments = shiftAssignmentRepository.getAssignmentsByWorkWeekId(workWeekId).first()
                    .filter { it.status == AssignmentStatus.CONFIRMED.name }.map { it.toDomain() }

            val schedulingData = SchedulingData(
                shifts = shifts,
                employees = employees,
                constraints = positiveConstraints,
                existingAssignments = existingAssignments,
                workWeek = workWeek
            )

            Result.success(schedulingData)

        } catch (e: Exception) {
            Result.failure(Exception("שגיאה בחילוץ נתונים: ${e.localizedMessage}"))
        }
    }
}



data class SchedulingData(
    val shifts: List<Shift> = emptyList(),
    val employees: List<Employee> = emptyList(),
    val constraints: List<EmployeeConstraint> = emptyList(),
    val existingAssignments: List<ShiftAssignment> = emptyList(),
    val workWeek: WorkWeek? = null
) {

    /**
     * פונקציות עזר לאלגוריתם השיבוץ
     */

    // משמרות שעדיין צריכות עובדים
    fun getUnfilledShifts(): List<Shift> {
        val assignmentCounts = existingAssignments.groupBy { it.shiftId }

        return shifts.filter { shift ->
            val currentAssigned = assignmentCounts[shift.id]?.size ?: 0
            currentAssigned < shift.employeesRequired
        }
    }

    // עובדים שיכולים לקבל עוד משמרות
    fun getAvailableEmployees(): List<Employee> {
        val currentAssignments = existingAssignments.groupBy { it.employeeId }

        return employees.filter { employee ->
            val currentShiftCount = currentAssignments[employee.id]?.size ?: 0
            currentShiftCount < employee.maxShifts
        }
    }

    // עובדים שיכולים לעבוד במשמרת ספציפית (לפי אילוצים חיוביים)
    fun getAvailableEmployeesForShift(shiftId: Long): List<Employee> {
        // עובדים שיש להם אילוץ חיובי למשמרת הזו
        val employeesWithPositiveConstraint = constraints
            .filter { it.shiftId == shiftId && it.canWork }
            .map { it.employeeId }
            .toSet()

        return getAvailableEmployees().filter { employee ->
            // אם יש אילוצים מוגדרים - רק עובדים עם אילוץ חיובי
            // אם אין אילוצים - כל העובדים יכולים (default)
            if (constraints.any { it.shiftId == shiftId }) {
                employee.id in employeesWithPositiveConstraint
            } else {
                true // אין אילוצים מוגדרים = כולם יכולים
            }
        }
    }

    // כמה עובדים חסרים למשמרת
    fun getMissingEmployeesCount(shiftId: Long): Int {
        val shift = shifts.find { it.id == shiftId } ?: return 0
        val currentAssigned = existingAssignments.count { it.shiftId == shiftId }
        return maxOf(0, shift.employeesRequired - currentAssigned)
    }

    // כמה משמרות עוד יכול לקבל עובד
    fun getRemainingShiftCapacity(employeeId: Long): Int {
        val employee = employees.find { it.id == employeeId } ?: return 0
        val currentAssigned = existingAssignments.count { it.employeeId == employeeId }
        return maxOf(0, employee.maxShifts - currentAssigned)
    }

    // בדיקה האם עובד יכול לעבוד במשמרת (לפי אילוצים חיוביים)
    fun canEmployeeWorkShift(employeeId: Long, shiftId: Long): Boolean {
        val hasPositiveConstraint = constraints.any { constraint ->
            constraint.employeeId == employeeId &&
                    constraint.shiftId == shiftId &&
                    constraint.canWork
        }

        val hasAnyConstraintForShift = constraints.any { it.shiftId == shiftId }

        return if (hasAnyConstraintForShift) {
            // יש אילוצים למשמרת - רק עם אילוץ חיובי
            hasPositiveConstraint
        } else {
            // אין אילוצים למשמרת - כולם יכולים
            true
        }
    }

    // בדיקה האם עובד כבר משובץ למשמרת
    fun isEmployeeAlreadyAssigned(employeeId: Long, shiftId: Long): Boolean {
        return existingAssignments.any {
            it.employeeId == employeeId && it.shiftId == shiftId
        }
    }

    fun getSummary(): String {
        val unfilledShifts = getUnfilledShifts()
        val availableEmployees = getAvailableEmployees()

        return buildString {
            appendLine("=== סיכום נתוני השיבוץ ===")
            appendLine("שבוע: ${workWeek?.name ?: "לא ידוע"}")
            appendLine("משמרות: ${shifts.size} (${unfilledShifts.size} צריכות עובדים)")
            appendLine("עובדים: ${employees.size} (${availableEmployees.size} זמינים)")
            appendLine("אילוצים חיוביים: ${constraints.size}")
            appendLine("שיבוצים קיימים: ${existingAssignments.size}")
            appendLine()

            unfilledShifts.forEach { shift ->
                val missing = getMissingEmployeesCount(shift.id)
                val availableForShift = getAvailableEmployeesForShift(shift.id).size
                appendLine("${shift.shiftDay} ${shift.shiftType}: חסרים $missing, זמינים $availableForShift")
            }
        }
    }
}
