package com.example.stopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stopwatch.ui.screens.AlarmScreen
import com.example.stopwatch.ui.screens.StopwatchScreen
import com.example.stopwatch.ui.screens.TimerScreen
import com.example.stopwatch.ui.screens.WorldClockScreen
import com.example.stopwatch.ui.theme.StopWatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StopWatchTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: "world_clock"

                Scaffold(
                    bottomBar = {
                        NavigationBar(containerColor = Color.White) {
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Public, "World Clock") },
                                label = { Text("Clock") },
                                selected = currentRoute == "world_clock",
                                onClick = { navController.navigate("world_clock") { launchSingleTop = true } }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Alarm, "Alarm") },
                                label = { Text("Alarm") },
                                selected = currentRoute == "alarm",
                                onClick = { navController.navigate("alarm") { launchSingleTop = true } }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.Timer, "Stopwatch") },
                                label = { Text("Stopwatch") },
                                selected = currentRoute == "stopwatch",
                                onClick = { navController.navigate("stopwatch") { launchSingleTop = true } }
                            )
                            NavigationBarItem(
                                icon = { Icon(Icons.Default.HourglassEmpty, "Timer") },
                                label = { Text("Timer") },
                                selected = currentRoute == "timer",
                                onClick = { navController.navigate("timer") { launchSingleTop = true } }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "world_clock",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("world_clock") { WorldClockScreen() }
                        composable("alarm") { AlarmScreen() }
                        composable("stopwatch") { StopwatchScreen() }
                        composable("timer") { TimerScreen() }
                    }
                }
            }
        }
    }
}
