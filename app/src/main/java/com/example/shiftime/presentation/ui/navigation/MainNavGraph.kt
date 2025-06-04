package com.example.shiftime.presentation.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.example.shiftime.presentation.ui.view.createchedulescreen.CreateScheduleScreen
import com.example.shiftime.presentation.ui.view.homescreen.EnhancedHomeScreen

@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainNavigationGraph(navController: NavController) {
    navigation(
        startDestination = ShiftTimeDestinations.HOME,
        route = ShiftTimeDestinations.MAIN_GRAPH
    ) {
        composable(ShiftTimeDestinations.HOME) {
            EnhancedHomeScreen(
                onNavigateToEmployees = {
                    navController.navigate(ShiftTimeDestinations.EMPLOYEES_GRAPH)
                },
                onNavigateToShifts = {
                    navController.navigate(ShiftTimeDestinations.SHIFTS_GRAPH)
                },
                onNavigateToSettings = {
                    navController.navigate(ShiftTimeDestinations.SETTINGS)
                },
                onNavigateToReports = {
                    navController.navigate(ShiftTimeDestinations.EMPLOYEE_DETAILS_ROUTE) // ← תוקן
                },
                onNavigateToConstraints = {
                    navController.navigate(ShiftTimeDestinations.EMPLOYEE_GRID_FOR_CONSTRAINTS)
                },
                navController = navController
            )
        }
        composable(ShiftTimeDestinations.CREATE_SCHEDULE){
            CreateScheduleScreen()
        }
    }
}