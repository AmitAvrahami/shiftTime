package com.example.shiftime.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.shiftime.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "work_weeks")
@TypeConverters(DateConverter::class)
data class WorkWeekEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val startDate: Date,
    val endDate: Date,
    val isActive: Boolean
)