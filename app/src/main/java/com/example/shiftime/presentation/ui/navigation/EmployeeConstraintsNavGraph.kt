package com.example.shiftime.presentation.ui.navigation


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shiftime.presentation.ui.view.employeeconstraintsscreen.EmployeeConstraintsScreen
import com.example.shiftime.presentation.ui.view.employeescreen.EmployeeScreen
import com.example.shiftime.presentation.ui.view.employeeselectionscreen.EmployeeSelectionScreen

fun NavGraphBuilder.employeeNavigationGraph(navController: NavController) {
    navigation(
        startDestination = ShiftTimeDestinations.EMPLOYEE_LIST, //TODO: CHANGE TO EMPLOYEE LIST
        route = ShiftTimeDestinations.EMPLOYEES_GRAPH //TODO: CHANGE TO EMPLOYEES GRAPH
    ){

        composable(ShiftTimeDestinations.EMPLOYEE_LIST) {
            EmployeeScreen()
        }

        composable(ShiftTimeDestinations.EMPLOYEE_GRID_FOR_CONSTRAINTS){ navBackStackEntry ->
            EmployeeSelectionScreen(
                onNavigateToEmployeeConstraints = { employeeId ->
                    navController.navigate(ShiftTimeDestinations.employeeConstraints(employeeId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = ShiftTimeDestinations.EMPLOYEE_CONSTRAINTS_ROUTE,
            arguments = listOf(
                navArgument("employeeId") {
                    type = NavType.LongType
                    defaultValue = 0L
                }
            )
        ) { navBackStackEntry ->
            EmployeeConstraintsScreen(
                employeeId = navBackStackEntry.arguments?.getLong("employeeId") ?: 0L,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}