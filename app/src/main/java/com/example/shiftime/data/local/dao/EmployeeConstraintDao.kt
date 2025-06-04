package com.example.shiftime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shiftime.data.local.entity.EmployeeConstraintEntity
import com.example.shiftime.utils.enums.Days
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeConstraintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConstraint(constraint: EmployeeConstraintEntity)

    @Query("DELETE FROM employee_constraints WHERE employeeId = :employeeId AND shiftId = :shiftId")
    suspend fun deleteConstraint(employeeId: Long, shiftId: Long)

    @Query("SELECT * FROM employee_constraints WHERE employeeId = :employeeId")
    fun getConstraintsByEmployeeId(employeeId: Long): Flow<List<EmployeeConstraintEntity>>

    @Query("""
        SELECT ec.* FROM employee_constraints ec
        INNER JOIN shifts s ON ec.shiftId = s.id
        WHERE ec.employeeId = :employeeId
        AND s.shiftDay = :day
        AND s.workWeekId = :workWeekId
    """)
    fun getConstraintsByEmployeeAndDay(employeeId: Long, day: Days, workWeekId: Long): Flow<List<EmployeeConstraintEntity>>

    @Query("""
        SELECT ec.* FROM employee_constraints ec
        INNER JOIN shifts s ON ec.shiftId = s.id
        WHERE ec.employeeId = :employeeId
        AND s.workWeekId = :workWeekId
    """)
    fun getConstraintsByEmployeeAndWorkWeek(employeeId: Long, workWeekId: Long): Flow<List<EmployeeConstraintEntity>>

    @Query("SELECT * FROM employee_constraints ec INNER JOIN shifts s ON ec.shiftId = s.id WHERE s.workWeekId = :workWeekId ")
    fun getConstraintsByWorkWeekId(workWeekId: Long): Flow<List<EmployeeConstraintEntity>>
}