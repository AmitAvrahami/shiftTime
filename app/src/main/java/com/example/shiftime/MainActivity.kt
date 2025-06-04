package com.example.shiftime

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.ShifTimeTheme
import com.example.shiftime.presentation.ui.navigation.AppNavigation
import com.example.shiftime.presentation.ui.view.createchedulescreen.CreateScheduleScreen
import com.example.shiftime.presentation.ui.view.employeeselectionscreen.EmployeeSelectionScreen
import com.example.shiftime.presentation.ui.view.homescreen.EnhancedHomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShifTimeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
//                    EmployeeScreen()
//                    CreateScheduleScreen()
//                    EmployeeSelectionScreen()
                     AppNavigation()
//                    EnhancedHomeScreen()

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShifTimeTheme {
        Greeting("Android")
    }
}


