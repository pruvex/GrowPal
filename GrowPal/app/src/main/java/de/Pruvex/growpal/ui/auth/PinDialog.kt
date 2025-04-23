package de.Pruvex.growpal.ui.auth

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import de.Pruvex.growpal.util.PinStorage
import de.Pruvex.growpal.util.BiometricHelper

@Composable
fun PinDialog(
    show: Boolean,
    onPinSuccess: () -> Unit,
    onForgotPin: () -> Unit,
    onDismiss: () -> Unit,
    mode: PinDialogMode = PinDialogMode.Enter
) {
    if (!show) return
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var pinRepeat by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val biometricAvailable = BiometricHelper.isBiometricAvailable(context)
    var enableBiometric by rememberSaveable { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (mode) {
                    PinDialogMode.Enter -> "PIN eingeben"
                    PinDialogMode.Set -> "Neue PIN festlegen"
                }
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = pin,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) pin = it
                        error = null
                    },
                    label = { Text("PIN") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )
                if (mode == PinDialogMode.Set) {
                    OutlinedTextField(
                        value = pinRepeat,
                        onValueChange = {
                            if (it.length <= 6 && it.all { c -> c.isDigit() }) pinRepeat = it
                            error = null
                        },
                        label = { Text("PIN wiederholen") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        visualTransformation = VisualTransformation.None,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    if (biometricAvailable) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = enableBiometric,
                                onCheckedChange = { enableBiometric = it }
                            )
                            Text("Biometrische Entsperrung aktivieren")
                        }
                    }
                }
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (mode == PinDialogMode.Enter) {
                    val storedPin = PinStorage.getPin(context)
                    if (storedPin == pin) {
                        onPinSuccess()
                        pin = ""
                        error = null
                    } else {
                        error = "Falsche PIN!"
                    }
                } else {
                    if (pin.length < 4) {
                        error = "PIN muss mindestens 4 Ziffern haben"
                    } else if (pin != pinRepeat) {
                        error = "PINs stimmen nicht überein"
                    } else {
                        PinStorage.savePin(context, pin)
                        if (enableBiometric && biometricAvailable) {
                            BiometricHelper.setBiometricEnabled(context, true)
                        } else {
                            BiometricHelper.setBiometricEnabled(context, false)
                        }
                        onPinSuccess()
                        pin = ""
                        pinRepeat = ""
                        error = null
                    }
                }
            }) {
                Text(
                    when (mode) {
                        PinDialogMode.Enter -> "Entsperren"
                        PinDialogMode.Set -> "Festlegen"
                    }
                )
            }
        },
        dismissButton = {
            if (mode == PinDialogMode.Enter) {
                TextButton(onClick = onForgotPin) { Text("PIN vergessen?") }
            } else {
                TextButton(onClick = onDismiss) { Text("Abbrechen") }
            }
        }
    )
}

enum class PinDialogMode { Enter, Set }
