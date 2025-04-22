package de.Pruvex.growpal.ui.settings

// Imports bleiben gleich
import android.app.Activity // Activity wird nicht mehr direkt benötigt, da recreate im Lambda in MainActivity ist
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // Wird nicht mehr direkt benötigt, aber kann bleiben
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.Pruvex.growpal.R // Stelle sicher, dass der R-Import korrekt ist
// LocaleHelper wird hier nicht mehr direkt für setLocale benötigt, nur für String-Ressourcen
// import de.Pruvex.growpal.util.LocaleHelper

@Composable
fun SettingsScreen(
    onLogout: () -> Unit, // Logout-Callback
    onLanguageSelected: (String) -> Unit // Lambda für Sprachauswahl
) {
    // val context = LocalContext.current // Wird nicht mehr benötigt
    // val activity = context as? Activity // Wird nicht mehr benötigt

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo oben einfügen
        Image(
            painter = painterResource(id = R.drawable.growpal_logo),
            contentDescription = stringResource(id = de.Pruvex.growpal.R.string.app_name),
            modifier = Modifier
                .size(120.dp)
                .padding(top = 8.dp, bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )


        // Sprachauswahl Sektion
        Text(
            text = stringResource(id = R.string.settings_language_label),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
        ) {
            // Button für Deutsch
            Button(onClick = {
                onLanguageSelected("de") // Rufe das übergebene Lambda auf
            }) {
                Text(text = stringResource(id = R.string.settings_language_german))
            }

            // Button für Englisch
            Button(onClick = {
                onLanguageSelected("en") // Rufe das übergebene Lambda auf
            }) {
                Text(text = stringResource(id = R.string.settings_language_english))
            }
        }
        Text(
            text = stringResource(id = R.string.settings_language_label),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp).align(Alignment.CenterHorizontally)
        )


        // Logout Sektion (Beispiel)
        Divider()
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout, // Verwende den Logout-Callback
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.logout))
        }

        // Hier können später weitere Einstellungen hinzugefügt werden
    }
}
