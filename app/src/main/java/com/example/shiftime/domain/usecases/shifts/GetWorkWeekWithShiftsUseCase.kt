package com.example.shiftime.domain.usecases.shifts

import com.example.shiftime.domain.model.WorkWeekWithShifts
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.domain.repository.WorkWeekRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetWorkWeekWithShiftsUseCase @Inject constructor(
    private val workWeekRepository: WorkWeekRepository,
    private val shiftRepository: ShiftRepository
) {
    fun getActiveWorkWeekWithShifts(): Flow<WorkWeekWithShifts?> {
        return workWeekRepository.observeActiveWorkWeek().combine(
            shiftRepository.getAllShifts()
        ) { workWeek, allShifts ->
            workWeek?.let { week ->
                val weekShifts = allShifts.filter { it.workWeekId == week.id }
                WorkWeekWithShifts(week, weekShifts)
            }
        }
    }

    fun getWorkWeekWithShifts(workWeekId: Long): Flow<WorkWeekWithShifts?> {
        return shiftRepository.getShiftsByWorkWeekId(workWeekId).map { shifts ->
            val workWeek = workWeekRepository.getWorkWeekById(workWeekId)
            workWeek?.let {
                WorkWeekWithShifts(it, shifts)
            }
        }
    }
}