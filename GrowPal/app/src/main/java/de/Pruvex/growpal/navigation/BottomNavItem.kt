package de.Pruvex.growpal.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Room
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItem("home", "Home", Icons.Filled.Home)
    object Rooms : BottomNavItem("rooms", "Räume", Icons.Filled.Room)
    object Diary : BottomNavItem("diary", "Tagebuch", Icons.Filled.Book)
    object Settings : BottomNavItem("settings", "Einstellungen", Icons.Filled.Settings)

    companion object {
        val items = listOf(Home, Rooms, Diary, Settings)
    }
}
