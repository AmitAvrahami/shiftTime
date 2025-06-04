package com.example.shiftime.data.local.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.shiftime.data.local.entity.ShiftEntity
import com.example.shiftime.domain.model.Employee
import com.example.shiftime.domain.model.Shift
import com.example.shiftime.domain.model.ShiftWithEmployees
import com.example.shiftime.utils.enums.Days
import com.example.shiftime.utils.enums.ShiftType
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

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


@RequiresApi(Build.VERSION_CODES.O)
fun Date.toLocalDate(): LocalDate {
    return this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

fun Shift.toShiftWithEmployees(employeesIdsToEmployee: (List<Long>) -> List<Employee>) : ShiftWithEmployees{
    //1.takeAllIds
    val employeeIds = this.assignedEmployees
    //2.covert The ids WIth the mapper function
    val employees = employeesIdsToEmployee(employeeIds)
    //3.return ShiftWithEmployees
    return ShiftWithEmployees(this, employees)
}

