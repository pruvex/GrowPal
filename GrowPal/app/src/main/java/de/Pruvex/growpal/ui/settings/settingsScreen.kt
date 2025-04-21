package de.Pruvex.growpal.ui.settings

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.Pruvex.growpal.R // Stelle sicher, dass der R-Import korrekt ist
import de.Pruvex.growpal.util.LocaleHelper // Importiere deinen LocaleHelper

@Composable
fun SettingsScreen(onLogout: () -> Unit) { // Logout-Callback hinzugefügt
    val context = LocalContext.current
    // Sicherstellen, dass der Context eine Activity ist, für recreate()
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start, // Linksbündig für Titel etc.
        verticalArrangement = Arrangement.spacedBy(8.dp) // Kleinerer Abstand
    ) {
        Text(
            text = stringResource(id = R.string.title_settings), // Verwende den allgemeinen Titel
            style = MaterialTheme.typography.headlineMedium, // Etwas größer für den Screen-Titel
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp) // Zentriert und mehr Abstand nach unten
        )

        // Sprachauswahl Sektion
        Text(
            text = stringResource(id = R.string.settings_language_title),
            style = MaterialTheme.typography.titleMedium // Titel für die Sektion
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), // Nimmt volle Breite
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally) // Zentriert mit Abstand
        ) {
            // Button für Deutsch
            Button(onClick = {
                LocaleHelper.setLocale(context, "de")
                activity?.recreate() // Activity neu starten
            }) {
                Text(text = stringResource(id = R.string.settings_language_german)) // Verwende String Ressource
            }

            // Button für Englisch
            Button(onClick = {
                LocaleHelper.setLocale(context, "en")
                activity?.recreate() // Activity neu starten
            }) {
                Text(text = stringResource(id = R.string.settings_language_english)) // Verwende String Ressource
            }
        }
        Text(
            text = stringResource(id = R.string.settings_language_note),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp).align(Alignment.CenterHorizontally) // Hinweis zentriert unter Buttons
        )


        // Logout Sektion (Beispiel)
        Divider() // Trennlinie
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout, // Verwende den Callback
            modifier = Modifier.align(Alignment.CenterHorizontally) // Zentriert
        ) {
            Text(text = stringResource(id = R.string.settings_logout))
        }

        // Hier können später weitere Einstellungen hinzugefügt werden
    }
}
