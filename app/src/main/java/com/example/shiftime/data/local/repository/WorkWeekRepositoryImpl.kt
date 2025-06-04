package com.example.shiftime.data.repository

import com.example.shiftime.data.local.dao.WorkWeekDao
import com.example.shiftime.data.local.entity.WorkWeekEntity
import com.example.shiftime.data.local.mapper.toDomain
import com.example.shiftime.data.local.mapper.toEntity
import com.example.shiftime.data.local.mapper.toDate
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.domain.repository.WorkWeekRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class WorkWeekRepositoryImpl @Inject constructor(
    private val workWeekDao: WorkWeekDao
) : WorkWeekRepository {
    override suspend fun createWorkWeek(workWeek: WorkWeek): WorkWeek {
        val entity = workWeek.toEntity()
        val id = workWeekDao.insertWorkWeek(entity)
        return workWeek.copy(id = id)
    }

    override suspend fun getWorkWeekById(id: Long): WorkWeek? {
        return workWeekDao.getWorkWeekById(id)?.toDomain()
    }

    override fun getActiveWorkWeek(): Flow<WorkWeekEntity?> {
        return workWeekDao.getActiveWorkWeek()
    }

    override suspend fun deactivateAllWorkWeeks() {
        workWeekDao.deactivateAllWorkWeeks()
    }

    override suspend fun setWorkWeekActive(id: Long, active: Boolean) {
        workWeekDao.setWorkWeekActive(id, active)
    }

    override fun observeActiveWorkWeek(): Flow<WorkWeek?> {
        return workWeekDao.observeActiveWorkWeek().map { it?.toDomain() }
    }

    override fun getAllWorkWeeks(): Flow<List<WorkWeek>> {
        return workWeekDao.getAllWorkWeeks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getWorkWeekByDate(date: LocalDate): Flow<WorkWeek?> {
        return workWeekDao.getWorkWeekByDate(date.toDate()).map { it?.toDomain() }
    }
}