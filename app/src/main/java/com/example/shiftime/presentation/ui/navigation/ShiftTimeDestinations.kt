package com.example.shiftime.presentation.ui.navigation

object ShiftTimeDestinations {
    // Main navigation
    const val MAIN_GRAPH = "main_graph"
    const val HOME = "home"

    // Employee management
    const val EMPLOYEES_GRAPH = "employees_graph"
    const val EMPLOYEE_LIST = "employee_list"
    const val EMPLOYEE_DETAILS = "employee_details"
    const val EMPLOYEE_CONSTRAINTS = "employee_constraints"
    const val ADD_EMPLOYEE = "add_employee"
    const val EDIT_EMPLOYEE = "edit_employee"
    const val EMPLOYEE_GRID_FOR_CONSTRAINTS = "employee_grid_for_constraints"

    // Shift management
    const val SHIFTS_GRAPH = "shifts_graph"
    const val SHIFT_LIST = "shift_list"
    const val CREATE_SCHEDULE = "create_schedule"
    const val SHIFT_ASSIGNMENT = "shift_assignment"

    // Settings
    const val SETTINGS_GRAPH = "settings_graph"
    const val SETTINGS = "settings"
    const val PROFILE = "profile"

    // Routes with parameters
    const val EMPLOYEE_DETAILS_ROUTE = "employee_details/{employeeId}"
    const val EMPLOYEE_CONSTRAINTS_ROUTE = "employee_constraints/{employeeId}"
    const val EDIT_EMPLOYEE_ROUTE = "edit_employee/{employeeId}"

    // Helper functions
    fun employeeDetails(employeeId: Long) = "employee_details/$employeeId"
    fun employeeConstraints(employeeId: Long) = "employee_constraints/$employeeId"
    fun editEmployee(employeeId: Long) = "edit_employee/$employeeId"
}