package de.Pruvex.growpal

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen // Import für Splashscreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import de.Pruvex.growpal.ui.auth.AuthScreen // Sicherstellen, dass AuthScreen importiert ist
import de.Pruvex.growpal.ui.auth.AuthViewModel // Import für AuthViewModel
import de.Pruvex.growpal.ui.settings.SettingsScreen // Sicherstellen, dass SettingsScreen importiert ist
import de.Pruvex.growpal.ui.theme.GrowPalTheme
import de.Pruvex.growpal.util.LocaleHelper // Import von LocaleHelper

// Definition der Screens für die Navigation
sealed class Screen(val route: String, val labelResId: Int, val icon: ImageVector) {
    object Auth : Screen("auth", R.string.app_name, Icons.Filled.AccountCircle) // Kein Label für Auth in BottomBar nötig
    object Home : Screen("home", R.string.bottom_nav_home, Icons.Filled.Home)
    object Rooms : Screen("rooms", R.string.bottom_nav_rooms, Icons.Filled.AccountCircle) // Beispiel-Icon
    object Diary : Screen("diary", R.string.bottom_nav_diary, Icons.Filled.DateRange) // Beispiel-Icon
    object Settings : Screen("settings", R.string.bottom_nav_settings, Icons.Filled.Settings)
}

class MainActivity : ComponentActivity() {

    // ViewModel mit activity-ktx holen
    private val authViewModel: AuthViewModel by viewModels()

    // Wichtig: attachBaseContext wird VOR onCreate aufgerufen
    override fun attachBaseContext(newBase: Context) {
        Log.d("MainActivity", "attachBaseContext called")
        val persistedLanguage = LocaleHelper.getPersistedLocale(newBase)
        Log.d("MainActivity", "Persisted language in attachBaseContext: $persistedLanguage")
        // Korrekter Aufruf von wrapContext mit zwei Argumenten
        val localeUpdatedContext: ContextWrapper = LocaleHelper.wrapContext(newBase, persistedLanguage)
        super.attachBaseContext(localeUpdatedContext)
        Log.d("MainActivity", "attachBaseContext finished. Current config: ${resources.configuration.locales[0]}")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        // Splashscreen installieren (benötigt core-splashscreen dependency)
        installSplashScreen()

        // Sprache beim Start setzen (redundant, wenn attachBaseContext funktioniert, aber schadet nicht für Logs)
        val currentLanguage = LocaleHelper.getPersistedLocale(this)
        Log.d("MainActivity", "Persisted language in onCreate: $currentLanguage")
        // LocaleHelper.updateAppLocale(currentLanguage) // Nicht hier, wird durch recreate() nach Wahl getriggert

        Log.d("MainActivity", "Current config in onCreate: ${resources.configuration.locales[0]}")


        setContent {
            // Aktuellen Auth-Status beobachten
            val authState by authViewModel.authState.collectAsState()
            val navController = rememberNavController()

            // Start-Ziel basierend auf Authentifizierungsstatus bestimmen
            // Wir müssen den initialen Status berücksichtigen, bevor LaunchedEffect greift
            val startDestination = remember(authState.isAuthenticated) {
                if (authState.isAuthenticated) Screen.Home.route else Screen.Auth.route
            }
            Log.d("MainActivity", "Setting up NavHost. Initial Start destination: $startDestination")

            GrowPalTheme {
                MainAppStructure(navController, authViewModel, startDestination, this)
            }
        }
    }
}


@Composable
fun MainAppStructure(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String,
    context: Context // Context für LocaleHelper übergeben
) {
    val items = listOf(
        Screen.Home,
        Screen.Rooms,
        Screen.Diary,
        Screen.Settings
    )
    // Zustand für Sichtbarkeit der BottomBar basierend auf der Route
    var showBottomBar by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()

    // Aktuelle Route beobachten, um BottomBar ein-/auszublenden
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        showBottomBar = items.any { it.route == currentRoute }
        Log.d("MainAppStructure", "Current route changed: $currentRoute, Show bottom bar: $showBottomBar")
    }

    // Effekt, der auf Änderungen im Authentifizierungsstatus reagiert, um Navigation zu steuern
    LaunchedEffect(authState.isAuthenticated, currentRoute) { // Auch currentRoute beobachten
        Log.d("MainAppStructure", "AuthState or Route changed. IsAuthenticated: ${authState.isAuthenticated}, CurrentRoute: $currentRoute")
        if (authState.isAuthenticated) {
            // Wenn authentifiziert und aktuell auf Auth-Screen, navigiere zu Home
            if (currentRoute == Screen.Auth.route) {
                Log.d("MainAppStructure", "Navigating to Home because authenticated and currently on Auth.")
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Auth.route) { inclusive = true } // Auth-Screen aus Backstack entfernen
                    launchSingleTop = true // Verhindert mehrfaches Starten von Home
                }
            } else if (currentRoute == null && startDestination == Screen.Home.route) {
                // Behandlung des initialen Starts, wenn bereits authentifiziert
                Log.d("MainAppStructure", "Initial state is authenticated, ensuring Home is displayed.")
                // Keine explizite Navigation nötig, da startDestination bereits Home ist
            }
        } else {
            // Wenn nicht authentifiziert und aktuell NICHT auf Auth-Screen, navigiere zu Auth
            if (currentRoute != Screen.Auth.route) {
                Log.d("MainAppStructure", "Navigating to Auth because not authenticated and not on Auth screen.")
                navController.navigate(Screen.Auth.route) {
                    // Alle anderen Screens aus dem Backstack entfernen
                    popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    launchSingleTop = true // Verhindert mehrfaches Starten von Auth
                }
            } else if (currentRoute == null && startDestination == Screen.Auth.route) {
                // Behandlung des initialen Starts, wenn nicht authentifiziert
                Log.d("MainAppStructure", "Initial state is unauthenticated, ensuring Auth is displayed.")
                // Keine explizite Navigation nötig, da startDestination bereits Auth ist
            }
        }
    }

    Scaffold(
        bottomBar = {
            // Nur anzeigen, wenn auf einem der Haupt-Screens
            if (showBottomBar) {
                NavigationBar {
                    items.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = stringResource(id = screen.labelResId)) },
                            label = { Text(stringResource(id = screen.labelResId)) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                Log.d("MainAppStructure", "Bottom nav clicked: ${screen.route}")
                                if (currentRoute != screen.route) { // Verhindere Navigation zum selben Ziel
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
            startDestination = startDestination, // Dynamischer Startpunkt
            modifier = Modifier.padding(innerPadding).fillMaxSize() // fillMaxSize hier empfohlen
        ) {
            composable(Screen.Auth.route) {
                Log.d("MainAppStructure", "Rendering AuthScreen")
                // AuthScreen erwartet onLoginClick und onRegisterClick
                AuthScreen(
                    onLoginClick = { email, password ->
                        Log.d("MainAppStructure", "AuthScreen: onLoginClick called")
                        authViewModel.loginUser(email, password)
                        // Navigation erfolgt durch LaunchedEffect oben
                    },
                    onRegisterClick = { email, password ->
                        Log.d("MainAppStructure", "AuthScreen: onRegisterClick called")
                        Log.w("MainAppStructure", "Register button clicked, but no navigation/action defined yet.")
                        // Hier ggf. zu Screen.Register.route navigieren oder ViewModel-Funktion aufrufen
                    }
                )
            }

            // --- Die anderen composable-Blöcke ---
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
                    onLogoutClick = {
                        Log.d("MainAppStructure", "SettingsScreen: onLogoutClick called")
                        authViewModel.logoutUser()
                        // Navigation zurück zum Auth-Screen passiert im LaunchedEffect
                    },
                    onLanguageSelected = { langCode ->
                        Log.d("MainAppStructure", "SettingsScreen: onLanguageSelected called with $langCode")
                        LocaleHelper.setLocale(context, langCode)
                        // Wichtig: Activity neu erstellen, damit attachBaseContext wirkt
                        val currentActivity = context as? Activity
                        currentActivity?.recreate() // Löst Neuladen mit neuer Locale aus
                        Log.d("MainAppStructure", "Activity recreated after language change.")
                    }
                )
            }
        }
    }
}


// Platzhalter für die Haupt-Screens
@Composable
fun PlaceholderScreen(name: String, navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.placeholder_content_for, name), // Verwende String-Formatierung
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
        )
        // Beispiel-Button, um zu den Einstellungen zu navigieren
        Button(
            onClick = { navController.navigate(Screen.Settings.route) },
            modifier = Modifier
                .align(androidx.compose.ui.Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Text(stringResource(id = R.string.go_to_settings_button))
        }
    }
}

// --- Preview --- (Optional, aber hilfreich für die Design-Phase)

// Preview benötigt oft eine vereinfachte Umgebung ohne echte Abhängigkeiten
@Preview(showBackground = true, locale = "de") // Locale für Preview setzen
@Composable
fun DefaultPreview() {
    GrowPalTheme {
        val navController = rememberNavController()
        // Für Preview verwenden wir einen Dummy-AuthViewModel oder initialisieren ihn einfach
        val previewAuthViewModel = AuthViewModel()
        // Starten wir die Preview auf dem Home-Screen (simuliert eingeloggt)
        MainAppStructure(navController, previewAuthViewModel, Screen.Home.route, androidx.compose.ui.platform.LocalContext.current)
    }
}

@Preview(showBackground = true, locale = "en") // Englische Preview
@Composable
fun AuthScreenPreview() {
    GrowPalTheme {
        AuthScreen(onLoginClick = {_,_ -> Log.d("Preview", "Login clicked") }, onRegisterClick = {_,_ -> Log.d("Preview", "Register clicked") })
    }
}