package com.example.shiftime.domain.usecases.shifts

import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.repository.WorkWeekRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAllWorkWeekUseCase @Inject constructor(
    private val workWeekRepository: WorkWeekRepository
) {
    operator fun invoke(): Flow<List<WorkWeek>> {
        return workWeekRepository.getAllWorkWeeks()
            .flowOn(Dispatchers.IO)
    }
}
