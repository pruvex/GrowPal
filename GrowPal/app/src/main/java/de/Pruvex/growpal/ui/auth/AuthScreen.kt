package de.Pruvex.growpal.ui.auth // Stelle sicher, dass dein package-Name korrekt ist

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.* // Beinhaltet remember, getValue, setValue
import androidx.compose.runtime.saveable.rememberSaveable // Besser für Zustandsüberleben bei Konfigurationsänderungen
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.Pruvex.growpal.R // Deine R-Datei für Ressourcen
import de.Pruvex.growpal.ui.theme.GrowPalTheme // Dein App-Theme

// --- Wichtiger Import für mutableStateOf, falls nicht durch '*' abgedeckt ---
// Normalerweise durch androidx.compose.runtime.* abgedeckt, aber explizit schadet nicht:
// import androidx.compose.runtime.mutableStateOf

@Composable
fun AuthScreen(
    onLoginClick: (String, String) -> Unit,
    onRegisterClick: (String, String) -> Unit, // Behalte dies, falls du einen Registrierungs-Button hast
    modifier: Modifier = Modifier
) {
    // rememberSaveable ist oft besser als remember, um den Zustand bei Drehungen etc. zu behalten
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- Korrigierte String-IDs ---
        Text(stringResource(id = R.string.login_welcome), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            // --- Korrigierte String-IDs ---
            label = { Text(stringResource(id = R.string.auth_email_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            // --- Korrigierte String-IDs ---
            label = { Text(stringResource(id = R.string.auth_password_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Diese Buttons verwenden die korrekten IDs
            Button(onClick = {
                // Führe hier ggf. eine kurze Validierung durch, bevor du klickst
                if (email.isNotBlank() && password.isNotBlank()) {
                    onLoginClick(email, password)
                } else {
                    // Optional: Zeige dem Benutzer eine Nachricht (z.B. Toast oder Snackbar)
                    // dass Felder nicht leer sein dürfen
                }
            }) {
                Text(stringResource(id = R.string.auth_login_button))
            }
            // Prüfe, ob du diesen Button hier brauchst oder die Registrierung woanders stattfindet
            Button(onClick = {
                // Ggf. Validierung
                if (email.isNotBlank() && password.isNotBlank()) {
                    onRegisterClick(email, password)
                } else {
                    // Optional: Fehlermeldung
                }
            }) {
                Text(stringResource(id = R.string.auth_register_button))
            }
        }
        // Hier könnten noch TextButtons für "Registrieren" oder "Passwort vergessen" hinzukommen
        // Spacer(modifier = Modifier.height(16.dp))
        // TextButton(onClick = { /* Navigiere zum Registrierungs-Screen */ }) {
        //     Text(stringResource(id = R.string.login_register_link)) // Beispiel
        // }
    }
}

// Die Preview sollte jetzt auch mit den korrekten Strings funktionieren
@Preview(showBackground = true, name = "Authentication Screen")
@Composable
fun AuthScreenPreview() {
    GrowPalTheme {
        AuthScreen(onLoginClick = { _, _ -> }, onRegisterClick = { _, _ -> })
    }
}