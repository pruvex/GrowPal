package de.Pruvex.growpal

import android.app.Activity
import android.content.Context
// ContextWrapper wird nicht mehr benötigt
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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

// Definition der Screens für die Navigation (unverändert)
sealed class Screen(val route: String, val labelResId: Int, val icon: ImageVector) {
    object Auth : Screen("auth", R.string.app_name, Icons.Filled.AccountCircle)
    object Home : Screen("home", R.string.bottom_nav_home, Icons.Filled.Home)
    object Rooms : Screen("rooms", R.string.bottom_nav_rooms, Icons.Filled.AccountCircle)
    object Diary : Screen("diary", R.string.bottom_nav_diary, Icons.Filled.DateRange)
    object Settings : Screen("settings", R.string.bottom_nav_settings, Icons.Filled.Settings)
}

class MainActivity : ComponentActivity() {

    // ViewModel mit activity-ktx holen
    private val authViewModel: AuthViewModel by viewModels()

    // --- KEINE attachBaseContext Methode hier! ---

    override fun onCreate(savedInstanceState: Bundle?) {
        // Sprache beim App-Start anwenden (via AppCompatDelegate)
        LocaleHelper.updateAppLocale(LocaleHelper.getPersistedLocale(this))
        Log.d("MainActivity", "Initial Locale set via AppCompatDelegate in onCreate")

        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called after super.onCreate")

        // Splashscreen installieren
        installSplashScreen()

        Log.d("MainActivity", "Current config in onCreate after initial setup: ${resources.configuration.locales.get(0)}") // .get(0) für neuere APIs

        setContent {
            val authState by authViewModel.authState.collectAsState()
            val navController = rememberNavController()

            // Navigation nach erfolgreichem Login
            LaunchedEffect(authState) {
                if (authState is AuthState.Success) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }

            GrowPalTheme {
                MainAppStructure(navController, authViewModel, authState, this)
            }
        }
    }
}


@Composable
fun MainAppStructure(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    authState: AuthState,
    context: Context // Context für recreate benötigt
) {
    val items = listOf(Screen.Home, Screen.Rooms, Screen.Diary, Screen.Settings)
    var showBottomBar by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val startDestination = if (authState is AuthState.Success) Screen.Home.route else Screen.Auth.route

    LaunchedEffect(currentRoute) {
        showBottomBar = items.any { it.route == currentRoute }
        Log.d("MainAppStructure", "Current route changed: $currentRoute, Show bottom bar: $showBottomBar")
    }

    LaunchedEffect(authState, currentRoute) {
        Log.d("MainAppStructure", "AuthState or Route changed. IsAuthenticated: ${authState is AuthState.Success}, CurrentRoute: $currentRoute")
        if (authState is AuthState.Success) {
            if (currentRoute == Screen.Auth.route) {
                Log.d("MainAppStructure", "Navigating to Home because authenticated and currently on Auth.")
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                    launchSingleTop = true
                }
            } else if (currentRoute == null && startDestination == Screen.Home.route) {
                Log.d("MainAppStructure", "Initial state is authenticated, ensuring Home is displayed.")
            }
        } else {
            if (currentRoute != Screen.Auth.route) {
                Log.d("MainAppStructure", "Navigating to Auth because not authenticated and not on Auth screen.")
                navController.navigate(Screen.Auth.route) {
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    launchSingleTop = true
                }
            } else if (currentRoute == null && startDestination == Screen.Auth.route) {
                Log.d("MainAppStructure", "Initial state is unauthenticated, ensuring Auth is displayed.")
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = stringResource(id = screen.labelResId)) },
                            label = { Text(stringResource(id = screen.labelResId)) },
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
            composable(Screen.Auth.route) {
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
                        onLoginClick = { email, password ->
                            Log.d("MainAppStructure", "AuthScreen: onLoginClick called")
                            authViewModel.login(email, password)
                        },
                        onRegisterClick = { email, password ->
                            Log.d("MainAppStructure", "AuthScreen: onRegisterClick called")
                            authViewModel.register(email, password)
                        },
                        modifier = Modifier.padding(padding)
                    )
                }
            }

            composable(Screen.Home.route) {
                Log.d("MainAppStructure", "Rendering PlaceholderScreen for Home")
                PlaceholderScreen("Home", navController)
            }
            composable(Screen.Rooms.route) {
                Log.d("MainAppStructure", "Rendering PlaceholderScreen for Rooms")
                PlaceholderScreen("Rooms", navController)
            }
            composable(Screen.Diary.route) {
                Log.d("MainAppStructure", "Rendering PlaceholderScreen for Diary")
                PlaceholderScreen("Diary", navController)
            }
            composable(Screen.Settings.route) {
                Log.d("MainAppStructure", "Rendering SettingsScreen")
                SettingsScreen(
                    onLogout = {
                        Log.d("MainAppStructure", "SettingsScreen: onLogout called")
                        authViewModel.logout()
                    },
                    onLanguageSelected = { langCode ->
                        Log.d("MainAppStructure", "SettingsScreen: onLanguageSelected called with $langCode")
                        LocaleHelper.setLocale(context, langCode)
                        val currentActivity = context as? Activity
                        currentActivity?.recreate()
                        Log.d("MainAppStructure", "Activity recreated after language change.")
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
            onClick = { navController.navigate(Screen.Settings.route) },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.TopEnd)
                .padding(16.dp) // dp import hinzugefügt
        ) {
            Text(stringResource(id = R.string.go_to_settings_button))
        }
    }
}

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    items: List<Screen>,
    currentRoute: String?
) {
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = null) },
                label = { Text(stringResource(id = item.labelResId)) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// --- Preview ---

@Preview(showBackground = true, locale = "de")
@Composable
fun DefaultPreview() {
    GrowPalTheme {
        val navController = rememberNavController()
        val previewAuthViewModel = AuthViewModel() // Annahme: ViewModel hat leeren Konstruktor für Preview
        MainAppStructure(
            navController,
            previewAuthViewModel,
            AuthState.Success(null), // Für Preview: userId ist null
            androidx.compose.ui.platform.LocalContext.current
        )
    }
}

@Preview(showBackground = true, locale = "en")
@Composable
fun AuthScreenPreview() {
    GrowPalTheme {
        AuthScreen(onLoginClick = {_,_ -> Log.d("Preview", "Login clicked") }, onRegisterClick = {_,_ -> Log.d("Preview", "Register clicked") })
    }
}
