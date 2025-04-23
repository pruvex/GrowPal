package de.Pruvex.growpal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.Pruvex.growpal.ui.HomeScreen
import de.Pruvex.growpal.ui.RoomsScreen
import de.Pruvex.growpal.ui.DiaryScreen
import de.Pruvex.growpal.ui.SettingsScreen
import de.Pruvex.growpal.navigation.BottomNavItem
import de.Pruvex.growpal.ui.theme.GrowPalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GrowPalTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("GrowPal") })
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Rooms.route) { RoomsScreen() }
            composable(BottomNavItem.Diary.route) { DiaryScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    // TODO: Implementiere die BottomNavigationBar wie in deinem alten Projekt
    // Hier kann ein Platzhalter stehen, falls die Komponente fehlt
    // Beispiel:
    // NavigationBar { ... }
}