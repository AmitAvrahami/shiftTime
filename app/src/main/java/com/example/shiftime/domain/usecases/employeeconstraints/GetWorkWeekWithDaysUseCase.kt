package com.example.shiftime.domain.usecases.employeeconstraints

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shiftime.domain.model.DayWithShifts
import com.example.shiftime.domain.repository.EmployeeConstraintRepository
import com.example.shiftime.domain.repository.WorkWeekRepository
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetWorkWeekWithDaysUseCase @Inject constructor(
    private val workWeekRepository: WorkWeekRepository,
    private val employeeConstraintRepository: EmployeeConstraintRepository
) {
    @RequiresApi(Build.VERSION_CODES.O)
    operator fun invoke(employeeId: Long, workWeekId: Long): Flow<List<DayWithShifts>> {
        return flow {
            val workWeek = workWeekRepository.getWorkWeekById(workWeekId)
            if (workWeek == null) {
                emit(emptyList())
                return@flow
            }

            val startDate = workWeek.startDate
            val daysList = mutableListOf<DayWithShifts>()

            for (i in 0..6) {
                val currentDate = startDate.plusDays(i.toLong())
                val day = Days.entries[i]

                val shifts = employeeConstraintRepository.getShiftsWithConstraintsByDay(
                    employeeId, day, workWeekId
                ).first()

                daysList.add(
                    DayWithShifts(
                        day = day,
                        date = currentDate,
                        shifts = shifts
                    )
                )
            }

            emit(daysList)
        }
    }
}