package com.example.shiftime.data.local.entity
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class EmployeeWithShifts(
    @Embedded val employee: EmployeeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ShiftAssignmentEntity::class,
            parentColumn = "employeeId",
            entityColumn = "shiftId"
        )
    )
    val shifts: List<ShiftEntity>
)