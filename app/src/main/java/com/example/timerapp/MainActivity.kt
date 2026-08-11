package com.example.timerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.timerapp.ui.screens.AboutScreen
import com.example.timerapp.ui.screens.PlaceholderScreen
import com.example.timerapp.ui.screens.SettingsScreen
import com.example.timerapp.ui.screens.TimerScreen
import com.example.timerapp.ui.theme.TimerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimerAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "timers"
    ) {
        composable("timers") {
            TimerScreen(onNavigate = { route ->
                navigateToRoute(navController, route)
            })
        }
        
        composable("products") {
            PlaceholderScreen(title = "Продукты")
        }
        
        composable("recipes") {
            PlaceholderScreen(title = "Рецепты")
        }
        
        composable("calculators") {
            PlaceholderScreen(title = "Калькуляторы")
        }
        
        composable("notes") {
            PlaceholderScreen(title = "Заметки")
        }
        
        composable("temperatures") {
            PlaceholderScreen(title = "Температуры")
        }
        
        composable("settings") {
            SettingsScreen(onNavigate = { route ->
                navigateToRoute(navController, route)
            })
        }
        
        composable("about") {
            AboutScreen(onNavigate = { route ->
                navigateToRoute(navController, route)
            })
        }
    }
}

private fun navigateToRoute(navController: NavHostController, route: String) {
    navController.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        popUpTo(navController.graph.startDestinationId) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }
}