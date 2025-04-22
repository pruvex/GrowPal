package de.Pruvex.growpal.ui.auth


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.Pruvex.growpal.R
import de.Pruvex.growpal.ui.theme.GrowPalTheme
import de.Pruvex.growpal.data.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    onLoginClick: (String, String, Boolean, android.content.Context) -> Unit,
    onRegisterClick: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    // rememberSaveable ist oft besser als remember, um den Zustand bei Drehungen etc. zu behalten
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var stayLoggedIn by remember { mutableStateOf(false) }
    // DataStore: Wert beim Start laden
    LaunchedEffect(Unit) {
        UserPreferences.stayLoggedInFlow(context).collect { stay ->
            stayLoggedIn = stay
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo oben einfügen
        Image(
            painter = painterResource(id = R.drawable.growpal_logo),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier
                .size(320.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = "Willkommen bei GrowPal!\nDeine Pflanzen. Dein Zuhause.",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp),
        )
        Text(stringResource(id = R.string.login_welcome), style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.auth_email_label)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        var passwordVisible by rememberSaveable { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.auth_password_label)) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                val description = if (passwordVisible) stringResource(id = R.string.hide_password) else stringResource(id = R.string.show_password)
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = stayLoggedIn,
                onCheckedChange = { stayLoggedIn = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.stay_logged_in))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        // DataStore: Wert speichern
                        scope.launch {
                            UserPreferences.setStayLoggedIn(context, stayLoggedIn)
                        }
                        onLoginClick(email, password, stayLoggedIn, context)
                    }
                },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Icon(Icons.Filled.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(id = R.string.auth_login_button))
            }
            Button(
                onClick = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onRegisterClick(email, password)
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Icon(Icons.Filled.AccountCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(id = R.string.auth_register_button))
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Wenn Sie keinen Account haben, wählen Sie eine E-Mail-Adresse und ein Passwort und klicken Sie auf Registrieren.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(vertical = 6.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Made by Pruvex",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp)
        )
    }
}

@Preview(showBackground = true, name = "Authentication Screen")
@Composable
fun AuthScreenPreview() {
    GrowPalTheme {
        AuthScreen(
            onLoginClick = { _, _, _, _ -> },
            onRegisterClick = { _, _ -> },
            modifier = Modifier
        )
    }
}