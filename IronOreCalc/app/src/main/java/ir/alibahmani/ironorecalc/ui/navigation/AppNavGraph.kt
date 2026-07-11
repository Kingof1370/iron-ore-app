package ir.alibahmani.ironorecalc.ui.navigation

import androidx.compose.animation.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ir.alibahmani.ironorecalc.ui.screens.*

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calculation : Screen("calculation")
    object History : Screen("history")
    object Diagram : Screen("diagram")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
    object About : Screen("about")
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = { fadeIn() + slideInHorizontally { -it / 4 } },
        exitTransition = { fadeOut() + slideOutHorizontally { it / 4 } },
        popEnterTransition = { fadeIn() + slideInHorizontally { it / 4 } },
        popExitTransition = { fadeOut() + slideOutHorizontally { -it / 4 } }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Calculation.route) {
            CalculationScreen(navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }
        composable(Screen.Diagram.route) {
            DiagramScreen(navController = navController)
        }
        composable(Screen.Reports.route) {
            ReportsScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.About.route) {
            AboutScreen(navController = navController)
        }
    }
}
