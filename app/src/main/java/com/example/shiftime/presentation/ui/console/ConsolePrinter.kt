//package com.example.shiftime.presentation.ui.console
//
//import com.example.shiftime.logic.data.ScheduleStatistics
//import com.example.shiftime.domain.model.Shift
//import com.example.shiftime.domain.model.ShiftSchedule
//import com.example.shiftime.domain.model.Employee
//
//
//object ConsolePrinter {
//    var debug: Boolean = true
//
//    fun printShiftSearchStart(shift: Shift, depth: Int) {
//        if (!debug) return
//        println("${indent(depth)}➡️ מחפש שיבוץ למשמרת: ${shift.shiftDay} - ${shift.shiftType} (${shift.employeesId.size}/${shift.employeesRequired})")
//    }
//
//    fun printTryingToAssign(employee: Employee, shift: Shift, depth: Int) {
//        if (!debug) return
//        println("${indent(depth)}🔄 מנסה לשבץ את ${employee.firstName} ${employee.lastName}")
//    }
//
//    fun printAssignSuccess(employee: Employee, shift: Shift, depth: Int) {
//        if (!debug) return
//        println("${indent(depth)}✅ שובץ: ${employee.firstName} למשמרת ${shift.shiftType}")
//    }
//
//    fun printBacktrackUnassign(employee: Employee, shift: Shift, depth: Int) {
//        if (!debug) return
//        println("${indent(depth)}↩️ ביטול שיבוץ של ${employee.firstName} ממשמרת ${shift.shiftType}")
//    }
//
//    fun printAssignmentFailure(employee: Employee, reasons: List<String>, depth: Int) {
//        if (!debug) return
//        println("${indent(depth)}⛔ ${employee.firstName} נפסל בגלל: ${reasons.joinToString(", ")}")
//    }
//
//    fun printUnfilledShiftNotice(shift: Shift, depth: Int) {
//        if (!debug) return
//        println("${indent(depth)}⚠️ לא נמצא עובד מתאים למשמרת ${shift.shiftType}, מדלגים...")
//    }
//
//    fun printFinalSchedule(schedule: ShiftSchedule) {
//        println("📅 סידור עבודה סופי:")
//        println(schedule)
//    }
//
//    fun printStatistics(statistics: ScheduleStatistics) {
//        statistics.printSummary()
//    }
//
//    fun printLine(char: Char = '-', count: Int = 80) {
//        if (!debug) return
//        println(char.toString().repeat(count))
//    }
//
//    private fun indent(depth: Int): String = "│  ".repeat(depth)
//}