package com.example.shiftime.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.shiftime.data.local.converters.DateConverter
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftStatus
import com.example.shiftime.utils.enums.ShiftType
import java.util.*

@Entity(
    tableName = "shifts",
    foreignKeys = [
        ForeignKey(
            entity = WorkWeekEntity::class,
            parentColumns = ["id"],
            childColumns = ["workWeekId"],
            onDelete = ForeignKey.CASCADE // מחיקת שבוע תמחק את כל המשמרות שלו
        )
    ],
    indices = [Index("workWeekId")] // אינדקס למהירות חיפוש
)
@TypeConverters(DateConverter::class)
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val workWeekId: Long,
    val shiftType: String, // נשמור כמחרוזת ונמיר אח"כ
    val shiftDay: String, // נשמור כמחרוזת ונמיר אח"כ
    val startTime: Date,
    val endTime: Date,
    val employeesRequired: Int
)