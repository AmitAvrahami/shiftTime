package com.example.shiftime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.data.local.entity.ShiftWithEmployeesEntity
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ShiftDao {

    // ✅ פעולות CRUD בסיסיות
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: ShiftEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShifts(shifts: List<ShiftEntity>): List<Long>

    @Update
    suspend fun updateShift(shift: ShiftEntity)

    @Delete
    suspend fun deleteShift(shift: ShiftEntity)

    @Query("DELETE FROM shifts WHERE id = :shiftId")
    suspend fun deleteShiftById(shiftId: Long)

    @Query("SELECT * FROM shifts WHERE id = :id")
    suspend fun getShiftById(id: Long): ShiftEntity?

    @Query("SELECT * FROM shifts ORDER BY startTime")
    fun getAllShifts(): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts WHERE workWeekId = :workWeekId ORDER BY startTime")
    fun getShiftsByWorkWeekId(workWeekId: Long): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts WHERE shiftDay = :day ORDER BY startTime")
    fun getShiftsByDay(day: String): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts WHERE shiftDay = :day AND workWeekId = :workWeekId ORDER BY startTime")
    fun getShiftsByDayAndWorkWeek(day: Days, workWeekId: Long): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime")
    fun getShiftsForDateRange(startDate: Date, endDate: Date): Flow<List<ShiftEntity>>

    @Query("""
        SELECT s.* FROM shifts s
        INNER JOIN work_weeks ww ON s.workWeekId = ww.id
        WHERE ww.isActive = 1 
        AND datetime(s.startTime / 1000, 'unixepoch') <= datetime('now')
        AND datetime(s.endTime / 1000, 'unixepoch') >= datetime('now')
        ORDER BY s.startTime DESC 
        LIMIT 1
    """)
    fun getCurrentShift(): Flow<ShiftEntity?>

    @Query("""
        SELECT s.* FROM shifts s
        INNER JOIN work_weeks ww ON s.workWeekId = ww.id
        WHERE ww.isActive = 1 
        AND datetime(s.startTime / 1000, 'unixepoch') > datetime('now')
        ORDER BY s.startTime ASC 
        LIMIT 1
    """)
    fun getNextShift(): Flow<ShiftEntity?>

    @Query("""
        SELECT s.* FROM shifts s
        INNER JOIN work_weeks ww ON s.workWeekId = ww.id
        WHERE ww.isActive = 1 
        AND date(s.startTime / 1000, 'unixepoch') = date('now')
        ORDER BY s.startTime ASC
    """)
    fun getTodayShifts(): Flow<List<ShiftEntity>>

    @Transaction
    @Query("""
        SELECT s.* FROM shifts s
        INNER JOIN work_weeks ww ON s.workWeekId = ww.id
        WHERE ww.isActive = 1 
        AND date(s.startTime / 1000, 'unixepoch') = date('now')
        ORDER BY s.startTime ASC
    """)
    fun getTodayShiftsWithEmployees(): Flow<List<ShiftWithEmployeesEntity>>

    @Query("SELECT COUNT(*) FROM shifts WHERE workWeekId = :workWeekId")
    suspend fun getShiftCountByWorkWeek(workWeekId: Long): Int

    @Query("""
        SELECT s.* FROM shifts s
        INNER JOIN work_weeks ww ON s.workWeekId = ww.id
        WHERE ww.isActive = 1 
        AND s.shiftDay = :day
        ORDER BY s.startTime ASC
    """)
    fun getActiveShiftsByDay(day: Days): Flow<List<ShiftEntity>>

    @Query("DELETE FROM shifts WHERE workWeekId = :workWeekId")
    suspend fun deleteShiftsByWorkWeek(workWeekId: Long)
}