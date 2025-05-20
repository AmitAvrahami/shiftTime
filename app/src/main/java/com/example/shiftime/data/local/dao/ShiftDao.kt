package com.example.shiftime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.shiftime.data.local.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ShiftDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShifts(shifts: List<ShiftEntity>): List<Long>

    @Update
    suspend fun updateShift(shift: ShiftEntity)

    @Query("SELECT * FROM shifts WHERE id = :id")
    suspend fun getShiftById(id: Long): ShiftEntity?

    @Query("SELECT * FROM shifts WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime")
    fun getShiftsForDateRange(startDate: Date, endDate: Date): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts WHERE shiftDay = :day ORDER BY startTime")
    fun getShiftsByDay(day: String): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts ORDER BY startTime")
    fun getAllShifts(): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts WHERE workWeekId = :workWeekId ORDER BY startTime")
    fun getShiftsByWorkWeekId(workWeekId: Long): Flow<List<ShiftEntity>>
}