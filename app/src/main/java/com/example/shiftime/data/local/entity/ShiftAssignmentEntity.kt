package com.example.shiftime.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.util.TableInfo
import com.example.shiftime.utils.enums.AssignmentStatus


@Entity(
    tableName = "shift_assignments",
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
    ],
    indices = [
        Index(value = ["employeeId"]),
        Index(value = ["shiftId"])
    ]
)
data class ShiftAssignmentEntity(
    val employeeId: Long,
    val shiftId: Long,
    val assignedAt: Long = System.currentTimeMillis(),
    val status: String = AssignmentStatus.ASSIGNED.name,
    val note: String? = null
)
