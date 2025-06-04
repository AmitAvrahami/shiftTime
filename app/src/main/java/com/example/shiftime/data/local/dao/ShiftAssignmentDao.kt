package com.example.shiftime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.shiftime.data.local.entity.EmployeeWithShiftsEntity
import com.example.shiftime.data.local.entity.ShiftAssignmentEntity
import com.example.shiftime.data.local.entity.ShiftWithEmployeesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftAssignmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: ShiftAssignmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignments(assignments: List<ShiftAssignmentEntity>)

    @Query("DELETE FROM shift_assignments WHERE employeeId = :employeeId AND shiftId = :shiftId")
    suspend fun deleteAssignment(employeeId: Long, shiftId: Long)

    @Query("DELETE FROM shift_assignments WHERE shiftId = :shiftId")
    suspend fun deleteAllAssignmentsForShift(shiftId: Long)

    @Query("SELECT * FROM shift_assignments WHERE shiftId = :shiftId")
    fun getAssignmentsForShift(shiftId: Long): Flow<List<ShiftAssignmentEntity>>

    @Query("SELECT * FROM shift_assignments WHERE employeeId = :employeeId")
    fun getAssignmentsForEmployee(employeeId: Long): Flow<List<ShiftAssignmentEntity>>

    @Query("SELECT COUNT(*) FROM shift_assignments WHERE shiftId = :shiftId")
    suspend fun getAssignmentCountForShift(shiftId: Long): Int

    @Transaction
    @Query("SELECT * FROM shifts WHERE id = :shiftId")
    fun getShiftWithAssignedEmployees(shiftId: Long): Flow<ShiftWithEmployeesEntity>

    @Transaction
    @Query("SELECT * FROM employees WHERE id = :employeeId")
    fun getEmployeeWithAssignedShifts(employeeId: Long): Flow<EmployeeWithShiftsEntity>

    @Query("SELECT * FROM shift_assignments sa INNER JOIN shifts s ON sa.shiftId = s.id WHERE s.workWeekId = :workWeekId")
    fun getAssignmentsByWorkWeekId(workWeekId: Long): Flow<List<ShiftAssignmentEntity>>


}