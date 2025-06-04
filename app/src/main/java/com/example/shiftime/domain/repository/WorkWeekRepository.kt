package com.example.shiftime.domain.repository


import com.example.shiftime.data.local.entity.WorkWeekEntity
import com.example.shiftime.domain.model.WorkWeek
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkWeekRepository {
    suspend fun createWorkWeek(workWeek: WorkWeek): WorkWeek
    suspend fun getWorkWeekById(id: Long): WorkWeek?
    fun getActiveWorkWeek(): Flow<WorkWeekEntity?>
    suspend fun deactivateAllWorkWeeks()
    suspend fun setWorkWeekActive(id: Long, active: Boolean)
    fun observeActiveWorkWeek(): Flow<WorkWeek?>
    fun getAllWorkWeeks(): Flow<List<WorkWeek>>
    fun getWorkWeekByDate(date: LocalDate): Flow<WorkWeek?>
}