package de.Pruvex.growpal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.Pruvex.growpal.ui.HomeScreen
import de.Pruvex.growpal.ui.RoomsScreen
import de.Pruvex.growpal.ui.DiaryScreen
import de.Pruvex.growpal.ui.SettingsScreen

@Composable
fun GrowPalNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        composable("rooms") { RoomsScreen() }
        composable("diary") { DiaryScreen() }
        composable("settings") { SettingsScreen() }
    }
}
