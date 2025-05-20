//package com.example.shiftime.logic.engine
//
//import com.example.shiftime.logic.data.ScheduleStatistics
//import com.example.shiftime.logic.data.ScoringWeights
//import com.example.shiftime.logic.BaseShiftAssigner
//import com.example.shiftime.logic.scoring.EmployeeScoring
//import com.example.shiftime.logic.rules.AlreadyAssignedRule
//import com.example.shiftime.logic.rules.AvailabilityRule
//import com.example.shiftime.logic.rules.MaxShiftRule
//import com.example.shiftime.logic.rules.RestRule
//import com.example.shiftime.domain.model.Employee
//import com.example.shiftime.domain.model.Shift
//import com.example.shiftime.domain.model.ShiftSchedule
//import com.example.shiftime.presentation.ui.console.ConsolePrinter
//
//class BacktrackingEngine(weights: ScoringWeights) : BaseShiftAssigner(weights)  {
//
//
//    private lateinit var schedule : ShiftSchedule
//    val rules = listOf(
//        AvailabilityRule(),
//        AlreadyAssignedRule(),
//        RestRule(minRestHours = 8.0),
//        MaxShiftRule()
//    )
//    val ruleEngine = RuleEngine(rules)
//
//
//    override fun assign(schedule: ShiftSchedule): Boolean {
//        this.schedule = schedule
//        return backtrack(depth = 0)
//
//    }
//
//
//
//
//    private fun backtrack(depth: Int = 0): Boolean {
//        if (isComplete(schedule)) {
//            ConsolePrinter.printFinalSchedule(schedule)
//            val stats = ScheduleStatistics.fromSchedule(schedule)
//            ConsolePrinter.printStatistics(stats)
//            return true
//        }
//
//        val nextShift = findNextShift() ?: return false
//        ConsolePrinter.printShiftSearchStart(nextShift, depth)
//
//        val sortedEmployees = sortEmployeesForShift(schedule, nextShift)
//        var assigned = false
//
//        for (employee in sortedEmployees) {
//            ConsolePrinter.printTryingToAssign(employee, nextShift, depth)
//            if (ruleEngine.isEmployeeValid(employee, nextShift)) {
//                assigned = true
//                assignEmployee(employee, nextShift)
//                ConsolePrinter.printAssignSuccess(employee, nextShift, depth)
//                if (backtrack(depth + 1)) return true
//
//                unassignEmployee(employee, nextShift)
//                ConsolePrinter.printBacktrackUnassign(employee, nextShift, depth)
//            } else {
//                val reasons = ruleEngine.debugEmployeeValidation(employee, nextShift)
//                ConsolePrinter.printAssignmentFailure(employee, reasons, depth)
//            }
//        }
//        if (!assigned) {
//            ConsolePrinter.printUnfilledShiftNotice(nextShift, depth)
//            nextShift.employeesId.add("SKIPPED") // אפשר להחליף בטיפול ריק
//            if (backtrack(depth + 1)) return true
//            nextShift.employeesId.remove("SKIPPED") // מחיקה אחרי ניסיון
//        }
//        return false
//    }
//
//    private fun isComplete(schedule: ShiftSchedule): Boolean {
//        return schedule.shifts.all { it.employeesId.size >= it.employeesRequired }
//    }
//
//    private fun findNextShift(): Shift? {
//        return schedule.shifts.maxByOrNull { it.employeesId.size < it.employeesRequired }
//    }
//
//    private fun sortEmployeesForShift(schedule: ShiftSchedule, shift: Shift): List<Employee> {
//        val idealMaxShifts = schedule.shifts.size / schedule.employees.size
//        return schedule.employees.sortedBy {
//            EmployeeScoring.calculateEmployeeScore(it, shift, idealMaxShifts, weights)
//        }
//    }
//
//
//
////    private fun isEmployeeOverMaxShift(employee: Employee): Boolean {
////        return employee.shifts.size >= employee.maxShifts
////    }
////
////    private fun isEmployeeRest(employee: Employee, shift: Shift, minRestHours: Double): Boolean {
////        val lastEmployeeShift = employee.shifts.maxByOrNull { it.endTime } ?: return true
////        if (employee.shifts.filter { it.shiftDay == shift.shiftDay }.size > 1) return false
////        var restTimeMillis = 0L
////        var restTimeHours = 0.0
////        if(shift.startTime.after(lastEmployeeShift.startTime)) {
////            restTimeMillis = (shift.startTime.time - lastEmployeeShift.endTime.time).absoluteValue
////            restTimeHours = restTimeMillis / (1000.0 * 60 * 60) // מילישניות -> שעות
////        }
////        else{
////            restTimeMillis = (lastEmployeeShift.startTime.time - shift.endTime.time).absoluteValue
////            restTimeHours = restTimeMillis / (1000.0 * 60 * 60) // מילישניות -> שעות
////        }
////        return restTimeHours >= minRestHours
////    }
////
//    private fun assignEmployee(employee: Employee, shift: Shift) {
//        schedule.addEmployeeToShift(employee.id, shift)
//    }
//
//    private fun unassignEmployee(employee: Employee, shift: Shift) {
//        schedule.removeEmployeeFromShift(employee.id, shift)
//    }
//
//    //TODO : MAKE INTERFACE FOR RULES
//
//}