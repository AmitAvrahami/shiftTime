package com.example.shiftime.data.mapper

import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.data.local.entity.WorkWeekEntity
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.WorkWeek
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

// מיפוי ישויות Shift
fun ShiftEntity.toDomain(): Shift {
    return Shift(
        id = id,
        workWeekId = workWeekId,
        shiftType = ShiftType.valueOf(shiftType),
        shiftDay = Days.valueOf(shiftDay),
        startTime = startTime,
        endTime = endTime,
        employeesRequired = employeesRequired,
        assignedEmployees = emptyList() // יש להשלים עם קשרי עובדים
    )
}

fun Shift.toEntity(): ShiftEntity {
    return ShiftEntity(
        id = id,
        workWeekId = workWeekId,
        shiftType = shiftType.name,
        shiftDay = shiftDay.name,
        startTime = startTime,
        endTime = endTime,
        employeesRequired = employeesRequired
    )
}


// פונקציות עזר להמרה בין תאריכים
fun Date.toLocalDate(): LocalDate {
    return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}