package com.example.shiftime.domain.usecases.shifts

import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.repository.ShiftRepository
import javax.inject.Inject

class UpdateShiftUseCase @Inject constructor(
    private val shiftRepository: ShiftRepository
) {
    suspend operator fun invoke(shift: Shift): Result<Shift> {
        return try {
            val updatedShift = shiftRepository.updateShift(shift)
            Result.success(updatedShift)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}