package com.example.shiftime.domain.usecases.shifts

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.model.WorkWeekWithShifts
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.usecases.shifts.command_pattern.ShiftOperations
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject


class ExecuteShiftOperationUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository
) {
    suspend operator fun invoke(operation: ShiftOperations): Result<String> {
        return try {
            val message = when (operation) {
                is ShiftOperations.Create -> {
                    shiftRepository.createShift(operation.shift)
                    "המשמרת נוצרה בהצלחה"
                }

                is ShiftOperations.Delete -> {
                    shiftRepository.deleteShift(operation.shiftId)
                    "המשמרת נמחקה בהצלחה"
                }

                is ShiftOperations.SaveShifts -> {
                    shiftRepository.saveShifts(operation.shifts)
                    "משמרות נשמרו בהצלחה"
                }

                is ShiftOperations.Update -> {
                    shiftRepository.updateShift(operation.updatedShift)
                    "משמרת עודכנה בהצלחה"
                }

            }

            Result.success(message)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    private fun createMorningShift(date: LocalDate, day: Days, workWeekId: Long): Shift {
        val calendar = Calendar.getInstance()
        calendar.time = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())

        // 6:45 - 14:45
        calendar.set(Calendar.HOUR_OF_DAY, 6)
        calendar.set(Calendar.MINUTE, 45)
        val startTime = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 45)
        val endTime = calendar.time

        return Shift(
            workWeekId = workWeekId,
            shiftType = ShiftType.MORNING,
            shiftDay = day,
            startTime = startTime,
            endTime = endTime,
            employeesRequired = 2
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createAfternoonShift(date: LocalDate, day: Days, workWeekId: Long): Shift {
        val calendar = Calendar.getInstance()
        calendar.time = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())

        // 14:45 - 22:45
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 45)
        val startTime = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 45)
        val endTime = calendar.time

        return Shift(
            workWeekId = workWeekId,
            shiftType = ShiftType.AFTERNOON,
            shiftDay = day,
            startTime = startTime,
            endTime = endTime,
            employeesRequired = 2
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNightShift(date: LocalDate, day: Days, workWeekId: Long): Shift {
        val calendar = Calendar.getInstance()
        calendar.time = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant())

        // 22:45 - 6:45 (יום למחרת)
        calendar.set(Calendar.HOUR_OF_DAY, 22)
        calendar.set(Calendar.MINUTE, 45)
        val startTime = calendar.time

        // יצירת קלנדר ליום שאחרי עבור שעת הסיום
        val nextDayCalendar = Calendar.getInstance()
        nextDayCalendar.time = calendar.time
        nextDayCalendar.add(Calendar.DAY_OF_MONTH, 1)
        nextDayCalendar.set(Calendar.HOUR_OF_DAY, 6)
        nextDayCalendar.set(Calendar.MINUTE, 45)
        val endTime = nextDayCalendar.time

        return Shift(
            workWeekId = workWeekId,
            shiftType = ShiftType.NIGHT,
            shiftDay = day,
            startTime = startTime,
            endTime = endTime,
            employeesRequired = 2
        )
    }

////    @RequiresApi(Build.VERSION_CODES.O)
//    private suspend fun generateShiftsForDay(startDate: LocalDate, name: String? = null): Result<WorkWeekWithShifts>  {
//        return try {
//            val endDate = startDate.plusDays(6)
//            val weekName = name ?: "שבוע ${startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
//
//            val workWeek = WorkWeek(
//                name = weekName,
//                startDate = startDate,
//                endDate = endDate,
//                isActive = true
//            )
//
//            workWeekRepository.deactivateAllWorkWeeks()
//
//            val savedWorkWeek = workWeekRepository.createWorkWeek(workWeek)
//
//            val shifts = mutableListOf<Shift>()
//
//            for (dayOffset in 0..6) {
//                val currentDate = startDate.plusDays(dayOffset.toLong())
//                val currentDay = Days.entries[dayOffset]
//
//                shifts.add(createMorningShift(currentDate, currentDay, savedWorkWeek.id))
//                shifts.add(createAfternoonShift(currentDate, currentDay, savedWorkWeek.id))
//                shifts.add(createNightShift(currentDate, currentDay, savedWorkWeek.id))
//            }
//
//            val savedShifts = shiftRepository.saveShifts(shifts)
//
//            Result.success(WorkWeekWithShifts(savedWorkWeek, savedShifts))
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}





