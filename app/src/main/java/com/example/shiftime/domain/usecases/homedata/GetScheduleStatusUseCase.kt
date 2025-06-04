package com.example.shiftime.domain.usecases.homedata

import com.example.shiftime.data.local.mapper.toShiftWithEmployees
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.ScheduleStatus
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.domain.model.WorkWeekWithShifts
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.usecases.employees.GetEmployeesUseCase
import com.example.shiftime.domain.usecases.shifts.GetWorkWeekWithShiftsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GetScheduleStatusUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val getEmployeesUseCase: GetEmployeesUseCase,
    private val getWorkWeekWithShiftsUseCase: GetWorkWeekWithShiftsUseCase,
) {

    operator fun invoke(): Flow<ScheduleStatus> {
        return combine(
            getEmployeesUseCase(),
            shiftRepository.getTodayShifts(),
            shiftRepository.getNextShift(),
            shiftRepository.getCurrentShift(),
            getWorkWeekWithShiftsUseCase()
        ) { employees, todayShifts, nextShift, currentShift, workWeekWithShifts ->
            ScheduleStatus(
                employees = employees,
                todayShifts = todayShifts,
                nextShift = nextShift?.let { shift ->
                    createShiftWithEmployees(shift, employees)
                },
                currentShift = currentShift?.let { shift ->
                    createShiftWithEmployees(shift, employees)
                },
                activeShifts = workWeekWithShifts?.shifts?.size ?: 0,
                pendingAssignments = calculatePendingAssignments(workWeekWithShifts),
                time = getCurrentTimeString(),
                todayActiveEmployees = getTodayActiveEmployees(employees, todayShifts)
            )
        }
    }

    private fun createShiftWithEmployees(shift: Shift, allEmployees: List<Employee>): ShiftWithEmployees {
        return shift.toShiftWithEmployees { assignedEmployeeIds ->
            allEmployees.filter { employee ->
                assignedEmployeeIds.contains(employee.id)
            }
        }
    }

    private fun getTodayActiveEmployees(allEmployees: List<Employee>, todayShifts: List<Shift>): List<Employee> {
        val todayActiveEmployeeIds = todayShifts
            .flatMap { it.assignedEmployees }
            .toSet()

        return allEmployees.filter { employee ->
            employee.id in todayActiveEmployeeIds
        }
    }

    private fun calculatePendingAssignments(workWeekWithShifts: WorkWeekWithShifts?): Int {
        return workWeekWithShifts?.shifts?.sumOf { shift ->
            val required = shift.employeesRequired
            val assigned = shift.assignedEmployees.size
            maxOf(0, required - assigned)
        } ?: 0
    }

    private fun getCurrentTimeString(): String {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            } else {
                java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                    .format(java.util.Date())
            }
        } catch (e: Exception) {
            ""
        }
    }
}

