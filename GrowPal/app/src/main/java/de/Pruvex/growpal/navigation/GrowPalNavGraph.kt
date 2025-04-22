package de.Pruvex.growpal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.Pruvex.growpal.ui.HomeScreen

@Composable
fun GrowPalNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen() }
        // Weitere Screens folgen modular
    }
}
