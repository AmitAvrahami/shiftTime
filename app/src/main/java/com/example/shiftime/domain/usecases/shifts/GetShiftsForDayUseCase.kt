package com.example.shiftime.domain.usecases.shifts

import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.repository.ShiftRepository
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetShiftsForDayUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository
) {
    operator fun invoke(day: Days): Flow<List<Shift>> {
        return shiftRepository.getShiftsByDay(day)
    }
}