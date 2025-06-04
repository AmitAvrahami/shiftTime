package com.example.shiftime.domain.usecases.shifts

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.model.WorkWeekWithShifts
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.repository.WorkWeekRepository
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import javax.inject.Inject

data class ShiftConfiguration(
    val shiftType: ShiftType,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val employeesRequired: Int = 2,
    val spansNextDay: Boolean = false
)

class CreateWorkWeekWithShiftsUseCase @Inject constructor(
    private val workWeekRepository: WorkWeekRepository,
    private val shiftRepository: ShiftRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    private val defaultShiftConfigurations = listOf(
        ShiftConfiguration(ShiftType.MORNING, LocalTime.of(6, 45), LocalTime.of(14, 45)),
        ShiftConfiguration(ShiftType.AFTERNOON, LocalTime.of(14, 45), LocalTime.of(22, 45)),
        ShiftConfiguration(ShiftType.NIGHT, LocalTime.of(22, 45), LocalTime.of(6, 45), spansNextDay = true)
    )

    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(
        startDate: LocalDate,
        name: String? = null,
        workDays: List<Days> = Days.entries.toList(),
        shiftConfigs: List<ShiftConfiguration> = defaultShiftConfigurations
    ): Result<WorkWeekWithShifts> {
        return try {
            val endDate = startDate.plusDays(6)
            val weekName = name ?: generateWeekName(startDate)

            val workWeek = WorkWeek(
                name = weekName,
                startDate = startDate,
                endDate = endDate,
                isActive = true
            )

            workWeekRepository.deactivateAllWorkWeeks()

            val savedWorkWeek = workWeekRepository.createWorkWeek(workWeek)

            val shifts = generateShiftsForWeek(savedWorkWeek.id, startDate, workDays, shiftConfigs)

            val saveResult = shiftRepository.saveShifts(shifts)

            saveResult.fold(
                onSuccess = { savedShifts ->
                    Result.success(WorkWeekWithShifts(savedWorkWeek, savedShifts))
                },
                onFailure = { error ->
                    Result.failure(Exception("שגיאה בעת שמירת המשמרות: ${error.message}"))
                }
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateShiftsForWeek(
        workWeekId: Long,
        startDate: LocalDate,
        workDays: List<Days>,
        shiftConfigs: List<ShiftConfiguration>
    ): List<Shift> {
        val shifts = mutableListOf<Shift>()

        for (dayOffset in 0..6) {
            val currentDate = startDate.plusDays(dayOffset.toLong())
            val currentDay = Days.entries[dayOffset]

            if (currentDay in workDays) {
                shifts.addAll(createDailyShifts(workWeekId, currentDate, currentDay, shiftConfigs))
            }
        }

        return shifts
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDailyShifts(
        workWeekId: Long,
        date: LocalDate,
        day: Days,
        shiftConfigs: List<ShiftConfiguration>
    ): List<Shift> {
        return shiftConfigs.map { config ->
            createShiftFromConfig(workWeekId, date, day, config)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createShiftFromConfig(
        workWeekId: Long,
        date: LocalDate,
        day: Days,
        config: ShiftConfiguration
    ): Shift {
        val startDateTime = LocalDateTime.of(date, config.startTime)
        val endDateTime = if (config.spansNextDay) {
            LocalDateTime.of(date.plusDays(1), config.endTime)
        } else {
            LocalDateTime.of(date, config.endTime)
        }

        return Shift(
            workWeekId = workWeekId,
            shiftType = config.shiftType,
            shiftDay = day,
            startTime = localDateTimeToDate(startDateTime),
            endTime = localDateTimeToDate(endDateTime),
            employeesRequired = config.employeesRequired
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateWeekName(startDate: LocalDate): String {
        return "שבוע ${startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }
}