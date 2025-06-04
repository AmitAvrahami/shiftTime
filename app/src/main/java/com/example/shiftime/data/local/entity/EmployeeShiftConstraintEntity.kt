package com.example.shiftime.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "employee_constraints",
    primaryKeys = ["employeeId", "shiftId"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ShiftEntity::class,
            parentColumns = ["id"],
            childColumns = ["shiftId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["employeeId"]),
        Index(value = ["shiftId"])
    ]
)
data class EmployeeConstraintEntity(
    val employeeId: Long,
    val shiftId: Long,
    val canWork: Boolean,
    val comment: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)