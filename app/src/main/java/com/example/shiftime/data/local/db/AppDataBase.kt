package com.example.shiftime.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.shiftime.data.local.converters.DateConverter
import com.example.shiftime.data.local.dao.EmployeeConstraintDao
import com.example.shiftime.data.local.dao.EmployeeDao
import com.example.shiftime.data.local.dao.ShiftAssignmentDao
import com.example.shiftime.data.local.dao.ShiftDao
import com.example.shiftime.data.local.dao.WorkWeekDao
import com.example.shiftime.data.local.entity.EmployeeConstraintEntity
import com.example.shiftime.data.local.entity.EmployeeEntity
import com.example.shiftime.data.local.entity.ShiftAssignmentEntity
import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.data.local.entity.WorkWeekEntity

@Database(
    entities = [
        EmployeeEntity::class,
        ShiftEntity::class,
        WorkWeekEntity::class,
        ShiftAssignmentEntity::class,
        EmployeeConstraintEntity::class,
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun shiftDao(): ShiftDao
    abstract fun workWeekDao(): WorkWeekDao
    abstract fun shiftAssignmentDao(): ShiftAssignmentDao
    abstract fun employeeConstraintDao(): EmployeeConstraintDao
}