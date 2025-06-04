package com.example.shiftime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import com.example.shiftime.data.local.converters.DateConverter
import com.example.shiftime.data.local.entity.WorkWeekEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
@TypeConverters(DateConverter::class)
interface WorkWeekDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkWeek(workWeek: WorkWeekEntity): Long

    @Update
    suspend fun updateWorkWeek(workWeek: WorkWeekEntity)

    @Query("SELECT * FROM work_weeks WHERE id = :id LIMIT 1")
    suspend fun getWorkWeekById(id: Long): WorkWeekEntity?

    @Query("SELECT * FROM work_weeks WHERE isActive = 1 LIMIT 1")
     fun getActiveWorkWeek():   Flow<WorkWeekEntity?>

    @Query("UPDATE work_weeks SET isActive = 0")
    suspend fun deactivateAllWorkWeeks()

    @Query("UPDATE work_weeks SET isActive = :active WHERE id = :id")
    suspend fun setWorkWeekActive(id: Long, active: Boolean)

    @Query("SELECT * FROM work_weeks WHERE isActive = 1 LIMIT 1")
    fun observeActiveWorkWeek(): Flow<WorkWeekEntity?>

    @Query("SELECT * FROM work_weeks ORDER BY startDate DESC")
    fun getAllWorkWeeks(): Flow<List<WorkWeekEntity>>

    @Query("SELECT * FROM work_weeks WHERE startDate <= :date AND endDate >= :date LIMIT 1")
    fun getWorkWeekByDate(date: Date): Flow<WorkWeekEntity?>
}