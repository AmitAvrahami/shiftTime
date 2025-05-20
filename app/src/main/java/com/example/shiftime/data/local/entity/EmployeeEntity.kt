package com.example.shiftime.data.local.entity

import androidx.compose.ui.res.painterResource
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shiftime.R
import com.example.shiftime.presentation.ui.common.state.EmployeeUiModel
import com.example.shiftime.presentation.ui.view.constraintscreen.UnavailableShift
import com.example.shiftime.utils.enums.Role

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val firstName: String = "",
    val lastName: String = "",
    val idNumber: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val dateOfBirth: Long = 0,  // אחסון תאריך כמספר
    val maxShifts: Int = 5,
    val minShifts: Int = 0,
    val totalWorkHoursLimit: Double = 40.0,
    val role: String = Role.REGULAR.name
)
