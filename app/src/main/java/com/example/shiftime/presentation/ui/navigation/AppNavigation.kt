package com.example.shiftime.presentation.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.shiftime.presentation.ui.view.createchedulescreen.CreateScheduleScreen
import com.example.shiftime.presentation.ui.view.employeeselectionscreen.EmployeeSelectionScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = ShiftTimeDestinations.MAIN_GRAPH
    ){
        mainNavigationGraph(navController)
        employeeNavigationGraph(navController)


    }
}

