package de.Pruvex.growpal

import android.app.Activity
import android.content.Context
// ContextWrapper wird nicht mehr benötigt
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import de.Pruvex.growpal.ui.HomeScreen
import de.Pruvex.growpal.ui.RoomsScreen
import de.Pruvex.growpal.ui.DiaryScreen
import de.Pruvex.growpal.ui.SettingsScreen
import de.Pruvex.growpal.navigation.BottomNavItem
import androidx.compose.ui.unit.dp // Import für dp hinzugefügt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Import für Splashscreen hinzugefügt
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import de.Pruvex.growpal.ui.auth.AuthScreen
import de.Pruvex.growpal.ui.auth.AuthViewModel
import de.Pruvex.growpal.ui.auth.AuthState
import de.Pruvex.growpal.ui.settings.SettingsScreen
import de.Pruvex.growpal.ui.theme.GrowPalTheme
import de.Pruvex.growpal.util.LocaleHelper
import androidx.compose.ui.tooling.preview.Preview
import de.Pruvex.growpal.data.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.google.firebase.auth.FirebaseAuth

// Definition der Screens für die Navigation (unverändert)


class MainActivity : AppCompatActivity() {

    // ViewModel mit activity-ktx holen
    private val authViewModel: AuthViewModel by viewModels()

    // --- KEINE attachBaseContext Methode hier! ---

    override fun onCreate(savedInstanceState: Bundle?) {
        // Sprache beim App-Start anwenden (via AppCompatDelegate)
        LocaleHelper.updateAppLocale(LocaleHelper.getPersistedLocale(this))
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setTheme(R.style.Theme_GrowPal)
        // DataStore: Prüfe, ob stay_logged_in aktiv ist
        var startDestination = "auth"
        runBlocking {
            val stayLoggedIn = UserPreferences.stayLoggedInFlow(this@MainActivity).first()
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            // Top-Dev: Robust gegen Build/ProGuard-Probleme – Singleton direkt, Fallback auf String
            try {
                val homeRoute = BottomNavItem.Home.route
                startDestination = if (stayLoggedIn && firebaseUser != null && !homeRoute.isNullOrEmpty()) {
                    homeRoute
                } else {
                    "auth"
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "BottomNavItem.Home.route nicht verfügbar (Build/ProGuard-Fehler?)", e)
                startDestination = "home" // Fallback auf String-Routenname
            }
            // Fallback: falls aus irgendeinem Grund startDestination leer oder null ist
            if (startDestination.isNullOrBlank() ||
                (startDestination != "auth" && BottomNavItem.items.none { it.route == startDestination })) {
                Log.e("MainActivity", "Ungültige startDestination: $startDestination, fallback auf 'auth'")
                startDestination = "auth"
            }
        }
        setContent {
            val authState by authViewModel.authState.collectAsState()
            val navController = rememberNavController()

            // Navigation nach erfolgreichem Login, aber nur wenn wir nicht schon auf Home sind
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    val homeRoute = try {
                        BottomNavItem.Home.route
                    } catch (e: Exception) {
                        Log.e("MainActivity", "BottomNavItem.Home.route nicht verfügbar", e)
                        "auth"
                    }
                    if (navController.currentDestination?.route != homeRoute && !homeRoute.isNullOrBlank()) {
                        navController.navigate(homeRoute) {
                            popUpTo(homeRoute) { inclusive = true }
                        }
                    }
                }
            }

            MainAppStructure(navController, authViewModel, authState, this, startDestination)
        }
    }
}


/**
 * Hauptstruktur der App mit Navigation und TopBar/BottomBar.
 * Zeigt AuthScreen, solange nicht eingeloggt.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun MainAppStructure(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    authState: AuthState,
    context: Context, // Context für recreate benötigt
    startDestination: String
) {
    val items = try {
        BottomNavItem.items
    } catch (e: Exception) {
        Log.e("MainAppStructure", "BottomNavItem.items nicht verfügbar", e)
        emptyList()
    }

    var showBottomBar by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val safeHomeRoute = try {
        BottomNavItem.Home.route
    } catch (e: Exception) {
        Log.e("MainAppStructure", "BottomNavItem.Home.route nicht verfügbar", e)
        "auth"
    }
    val startDestination = if (authState is AuthState.Success) safeHomeRoute else "auth"

    LaunchedEffect(currentRoute) {
        showBottomBar = items.any { it.route == currentRoute }
    }

    LaunchedEffect(authState, currentRoute) {
        val homeRoute = try {
            BottomNavItem.Home.route
        } catch (e: Exception) {
            Log.e("MainAppStructure", "BottomNavItem.Home.route nicht verfügbar", e)
            "auth"
        }
        if (authState is AuthState.Success) {
            if (currentRoute == "auth") {
                navController.navigate(homeRoute) {
                    popUpTo(homeRoute) { inclusive = true }
                    launchSingleTop = true
                }
            }
        } else {
            if (currentRoute != "auth") {
                navController.navigate("auth") {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (currentRoute != "auth") {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when (currentRoute) {
                                    BottomNavItem.Home.route -> stringResource(id = R.string.bottom_nav_home)
                                    BottomNavItem.Rooms.route -> stringResource(id = R.string.bottom_nav_rooms)
                                    BottomNavItem.Diary.route -> stringResource(id = R.string.bottom_nav_diary)
                                    BottomNavItem.Settings.route -> stringResource(id = R.string.bottom_nav_settings)
                                    else -> ""
                                },
                                maxLines = 1
                            )
                            if (currentRoute == BottomNavItem.Home.route) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Image(
                                    painter = painterResource(id = R.drawable.growpal_logo),
                                    contentDescription = stringResource(id = R.string.app_name),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    },
                    navigationIcon = {}
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                Log.d("MainAppStructure", "Bottom nav clicked: ${screen.route}")
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        ) {
            composable("auth") {
                Log.d("MainAppStructure", "Rendering AuthScreen")
                var snackbarHostState = remember { SnackbarHostState() }

                // Fehlerbehandlung
                if (authState is AuthState.Error) {
                    LaunchedEffect(authState) {
                        snackbarHostState.showSnackbar(authState.message)
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { padding ->
                    AuthScreen(
                        onLoginClick = { email, password, stayLoggedIn, context ->
                            Log.d("MainAppStructure", "AuthScreen: onLoginClick called")
                            authViewModel.login(email, password, stayLoggedIn, context)
                        },
                        onRegisterClick = { email, password ->
                            Log.d("MainAppStructure", "AuthScreen: onRegisterClick called")
                            authViewModel.register(email, password)
                        },
                        modifier = Modifier.padding(padding)
                    )
                }
            }

            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }
            composable(BottomNavItem.Rooms.route) {
                RoomsScreen()
            }
            composable(BottomNavItem.Diary.route) {
                DiaryScreen()
            }
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    onLogout = {
                        authViewModel.logout(context)
                    },
                    onLanguageSelected = { langCode ->
                        LocaleHelper.setLocale(context, langCode)
                        val currentActivity = context as? Activity
                        currentActivity?.recreate()
                    }
                )
            }
        }
    }
}


// Platzhalter (unverändert)
@Composable
fun PlaceholderScreen(name: String, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.placeholder_content_for, name),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
        )
        Button(
            onClick = {},
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.TopEnd)
                .padding(16.dp) 
        ) {
            Text(stringResource(id = R.string.go_to_settings_button))
        }
    }
}

