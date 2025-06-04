package com.example.shiftime.domain.usecases.employeeconstraints

import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.repository.WorkWeekRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for retrieving the currently active work week.
 *
 * This class encapsulates the business logic for fetching the active work week
 * from the underlying data source (repository). It exposes a suspend operator
 * function to be invoked, which returns a Flow of an optional [WorkWeek].
 *
 * The active work week is determined by the [WorkWeekRepository]. If no work week
 * is currently active, the Flow will emit `null`.
 *
 * @property workWeekRepository The repository responsible for accessing work week data.
 */
class GetActiveWorkWeekUseCase @Inject constructor(
    private val workWeekRepository: WorkWeekRepository
) {
    suspend operator fun invoke(): Flow<WorkWeek?> {
        return workWeekRepository.getActiveWorkWeek().map{
            it?.toDomain()
        }

    }
}
