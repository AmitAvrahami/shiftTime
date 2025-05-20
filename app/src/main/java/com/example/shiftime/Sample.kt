//package com.example.shiftime
//
//import android.annotation.SuppressLint
//import com.example.shiftime.utils.enums.Days
//import com.example.shiftime.domain.model.Employee
//import com.example.shiftime.domain.model.Shift
//import com.example.shiftime.domain.model.ShiftSchedule
//import com.example.shiftime.utils.enums.ShiftType
//import java.text.SimpleDateFormat
//import java.util.*
//import kotlin.math.absoluteValue
//
//// הגדרת תאריכים
//val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")
//
//fun createShift(day: Days, date: String, shiftType: ShiftType): Shift {
//    var employeesRequired = 2
//    val (start, end) = when (shiftType) {
//        ShiftType.MORNING -> dateFormat.parse("$date 06:45") to dateFormat.parse("$date 14:45")
//        ShiftType.AFTERNOON -> dateFormat.parse("$date 14:45") to dateFormat.parse("$date 22:45")
//        ShiftType.NIGHT -> {
//            employeesRequired = 1
//            val startNight = dateFormat.parse("$date 22:45")
//            val endNight = Calendar.getInstance().apply {
//                time = dateFormat.parse("$date 06:45")
//                add(Calendar.DATE, 1) // מוסיפים יום
//            }.time
//            startNight to endNight
//        }
//    }
//    employeesRequired = when(day){
//        Days.FRIDAY -> if(shiftType == ShiftType.MORNING)  2 else 1
//        Days.SATURDAY -> 1
//        else -> employeesRequired
//    }
//
//
//    return Shift(
//        id = UUID.randomUUID().toString(),
//        shiftType = shiftType,
//        shiftDay = day,
//        employeesRequired = employeesRequired, // לדוגמה - צריך שני עובדים בכל משמרת
//        startTime = start,
//        endTime = end,
//        description = "משמרת ${shiftType.name.lowercase()} ב-$day",
//    )
//}
//
//
//
//// כל המשמרות
//val shifts = listOf(
//    // ראשון 13/4
//    createShift(Days.SUNDAY, "13/04/2025", ShiftType.MORNING),
//    createShift(Days.SUNDAY, "13/04/2025", ShiftType.AFTERNOON),
//    createShift(Days.SUNDAY, "13/04/2025", ShiftType.NIGHT),
//
//    // שני 14/4
//    createShift(Days.MONDAY, "14/04/2025", ShiftType.MORNING),
//    createShift(Days.MONDAY, "14/04/2025", ShiftType.AFTERNOON),
//    createShift(Days.MONDAY, "14/04/2025", ShiftType.NIGHT),
//
//    // שלישי 15/4
//    createShift(Days.TUESDAY, "15/04/2025", ShiftType.MORNING),
//    createShift(Days.TUESDAY, "15/04/2025", ShiftType.AFTERNOON),
//    createShift(Days.TUESDAY, "15/04/2025", ShiftType.NIGHT),
//
//    // רביעי 16/4
//    createShift(Days.WEDNESDAY, "16/04/2025", ShiftType.MORNING),
//    createShift(Days.WEDNESDAY, "16/04/2025", ShiftType.AFTERNOON),
//    createShift(Days.WEDNESDAY, "16/04/2025", ShiftType.NIGHT),
//
//    // חמישי 17/4
//    createShift(Days.THURSDAY, "17/04/2025", ShiftType.MORNING),
//    createShift(Days.THURSDAY, "17/04/2025", ShiftType.AFTERNOON),
//    createShift(Days.THURSDAY, "17/04/2025", ShiftType.NIGHT),
//
//    // שישי 18/4
//    createShift(Days.FRIDAY, "18/04/2025", ShiftType.MORNING),
//    createShift(Days.FRIDAY, "18/04/2025", ShiftType.AFTERNOON),
//    createShift(Days.FRIDAY, "18/04/2025", ShiftType.NIGHT),
//
//    // שבת 19/4
//    createShift(Days.SATURDAY, "19/04/2025", ShiftType.MORNING),
//    createShift(Days.SATURDAY, "19/04/2025", ShiftType.AFTERNOON),
//    createShift(Days.SATURDAY, "19/04/2025", ShiftType.NIGHT)
//)
//
//// רשימת העובדים
//val employees = listOf(
//    Employee(
//        id = "1",
//        firstName = "עמית",
//        lastName = "אברהמי",
//        idNumber = "111111111",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 4,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(
//            shifts[0],
//            shifts[1],
//            shifts[2],
//            shifts[3],
//            shifts[4]
//        ) // כל המשמרות שעמית לא יכול לעבוד בהן
//    ),
//    Employee(
//        id = "2",
//        firstName = "מסי",
//        lastName = "כהן",
//        idNumber = "222222222",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 5,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(shifts[0], shifts[1], shifts[2])
//    ),
//    Employee(
//        id = "3",
//        firstName = "וולריה",
//        lastName = "רוזן",
//        idNumber = "333333333",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 3,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(
//            shifts[2],
//            shifts[3],
//            shifts[4],
//            shifts[6],
//            shifts[7],
//            shifts[8]
//        )
//    ),
//    Employee(
//        id = "4",
//        firstName = "מיטל",
//        lastName = "לוי",
//        idNumber = "444444444",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 5,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(shifts[3], shifts[6])
//    ),
//    Employee(
//        id = "5",
//        firstName = "פולינה",
//        lastName = "לוי",
//        idNumber = "55555555",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 5,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(shifts[3], shifts[6])
//    ),
//    Employee(
//        id = "6",
//        firstName = "שחר",
//        lastName = "לוי",
//        idNumber = "66666666",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 4,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(shifts[3], shifts[6])
//    ),
//    Employee(
//        id = "7",
//        firstName = "שני",
//        lastName = "לוי",
//        idNumber = "77777777",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 5,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(shifts[3], shifts[6])
//    ),
//    Employee(
//        id = "8",
//        firstName = "מלי",
//        lastName = "לוי",
//        idNumber = "88888888",
//        email = "",
//        phoneNumber = "",
//        address = "",
//        dateOfBirth = Date(),
//        maxShifts = 5,
//        minShifts = 3,
//        unavailableShifts = mutableListOf(shifts[3], shifts[6])
//    )
//)
//
//fun printUnavailableShifts(employees: List<Employee>) {
//    println("אילוצים - אילו עובדים לא יכולים לעבוד באילו משמרות:")
//    println("-".repeat(90))
//
//    for (employee in employees) {
//        println("עובד: ${employee.firstName} ${employee.lastName}")
//
//        if (employee.unavailableShifts.isEmpty()) {
//            println("   אין מגבלות! יכול לעבוד בכל המשמרות.")
//        } else {
//            for (shift in employee.unavailableShifts) {
//                val day = shift.shiftDay.name.lowercase().replaceFirstChar { it.uppercase() }
//                val shiftTypeName = when (shift.shiftType) {
//                    ShiftType.MORNING -> "בוקר"
//                    ShiftType.AFTERNOON -> "צהריים"
//                    ShiftType.NIGHT -> "לילה"
//                }
//                println("   ❌ $day - משמרת $shiftTypeName")
//            }
//        }
//
//        println("-".repeat(90))
//    }
//}
//
//fun printEmployees(employees: List<Employee>) {
//    println("כל העובדים:")
//    println("-".repeat(90))
//    for (employee in employees) {
//        println("עובד: ${employee.firstName} ${employee.lastName}")
//        println(employee)
//        println("!".repeat(90))
//    }
//}
//
//val schedule = ShiftSchedule(
//    shifts = shifts,
//    employees = employees
//)
//
//
//
//fun findNextShift(schedule: ShiftSchedule): Shift? {
//    val nextShift = schedule.shifts.maxByOrNull { it.employeesId.size < it.employeesRequired }
//    return nextShift
//}
//
//fun isValidEmployee(employee: Employee, shift: Shift): Boolean {
//    if (employee.unavailableShifts.contains(shift)) return false
//    if (shift.employeesId.contains(employee.id)) return false // כבר שובץ
//    if(!isEmployeeRest(employee, shift, 8.0)) return false
//    if(isEmployeeOverMaxShift(employee)) return false
//    return true
//}
//
//fun isEmployeeOverMaxShift(employee: Employee): Boolean {
//    return employee.shifts.size >= employee.maxShifts
//}
//
//
//fun isEmployeeRest(employee: Employee, shift: Shift, minRestHours: Double): Boolean {
//    val lastEmployeeShift = employee.shifts.maxByOrNull { it.endTime } ?: return true
//    if (employee.shifts.filter { it.shiftDay == shift.shiftDay }.size > 1) return false
//    var restTimeMillis = 0L
//    var restTimeHours = 0.0
//    if(shift.startTime.after(lastEmployeeShift.startTime)) {
//         restTimeMillis = (shift.startTime.time - lastEmployeeShift.endTime.time).absoluteValue
//         restTimeHours = restTimeMillis / (1000.0 * 60 * 60) // מילישניות -> שעות
//    }
//    else{
//         restTimeMillis = (lastEmployeeShift.startTime.time - shift.endTime.time).absoluteValue
//         restTimeHours = restTimeMillis / (1000.0 * 60 * 60) // מילישניות -> שעות
//    }
//    return restTimeHours >= minRestHours
//}
//
//
//fun totalWorkHours(employee: Employee): Double {
//    return employee.workHours.sum()
//}
//
//
//
//@SuppressLint("SuspiciousIndentation")
//fun backtrack(schedule: ShiftSchedule, depth: Int = 0): Boolean {
//    if (schedule.shifts.all { it.employeesId.size >= it.employeesRequired }) {
//        println("✔️ נמצאה הקצאה מלאה!")
//        println(schedule)
//        return true
//    }
//    val nextShift = findNextShift(schedule) ?: return false
//    printIndent(depth)
//    println("➡️ מחפש שיבוץ למשמרת: ${nextShift.shiftDay} - ${nextShift.shiftType} (${nextShift.employeesId.size}/${nextShift.employeesRequired})")
//    val sortedEmployees = schedule.sortEmployeesForShift(schedule, nextShift)
//    var assigned = false
//    for (employee in sortedEmployees) {
//        if (isValidEmployee(employee, nextShift)) {
//            assigned = true
//            printIndent(depth)
//            println("✅ מנסה לשבץ את העובד: ${employee.firstName} ${employee.lastName}")
//
//            schedule.addEmployeeToShift(employee.id, nextShift)
//
//            if (backtrack(schedule, depth + 1)) {
//                return true
//            }
//
//            // אם לא הצליח - מחזירים אחורה
//            printIndent(depth)
//            println("↩️ מבטל שיבוץ של העובד: ${employee.firstName} ${employee.lastName}")
//
//            schedule.removeEmployeeFromShift(employee.id, nextShift)
//        } else {
//            printIndent(depth)
//            println("⛔ עובד לא מתאים: ${employee.firstName} ${employee.lastName}")
//        }
//    }
//    if (!assigned) {
//        printIndent(depth)
//        println("⚠️ לא נמצא עובד מתאים למשמרת הזאת. מדלגים וממשיכים קדימה...")
//        schedule.addEmployeeToShift("",nextShift)
//        if (backtrack(schedule, depth + 1)) {
//            return true
//        }
//    }
//    return false
//}
//
//fun printIndent(depth: Int) {
//    repeat(depth) {
//        print("    ")
//    }
//}
//
//fun main(){
//    println(schedule)
//    schedule.scheduleMorningShifts("וולריה","מיטל")
//    val success = backtrack(schedule)
//    if (!success) {
//        println("❌ לא הצלחנו למצוא סידור עבודה חוקי.")
//    }
//    printUnavailableShifts(employees)
//}