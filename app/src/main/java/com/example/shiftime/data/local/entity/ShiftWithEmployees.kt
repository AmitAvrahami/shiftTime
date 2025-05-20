package com.example.shiftime.data.local.entity

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ShiftWithEmployees(
    @Embedded val shift: ShiftEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = ShiftAssignmentEntity::class,
            parentColumn = "shiftId",
            entityColumn = "employeeId"
        )
    )
    val employees: List<EmployeeEntity>
)