package de.Pruvex.growpal.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GrowPalGreenDark,
    secondary = GrowPalAccent,
    background = GrowPalGrey,
    surface = GrowPalGrey,
    onPrimary = GrowPalWhite,
    onSecondary = GrowPalText,
    onBackground = GrowPalText,
    onSurface = GrowPalText,
    error = GrowPalError
)

private val LightColorScheme = lightColorScheme(
    primary = GrowPalGreen,
    secondary = GrowPalAccent,
    background = GrowPalGrey,
    surface = GrowPalWhite,
    onPrimary = GrowPalWhite,
    onSecondary = GrowPalText,
    onBackground = GrowPalText,
    onSurface = GrowPalText,
    error = GrowPalError
)

@Composable
fun GrowPalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}