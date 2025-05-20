package com.example.shiftime.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
@Entity(
    tableName = "employee_shift_cross_ref",
    primaryKeys = ["employeeId", "shiftId"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"]
        ),
        ForeignKey(
            entity = ShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["shiftId"]
        )
    ]
)
data class EmployeeShiftCrossRef(
    val employeeId: Long,
    val shiftId: Long,
    val isAssigned: Boolean = true
)