package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = GazetteSecondaryVariant,
    onPrimary = Color.Black,
    primaryContainer = GazettePrimaryVariant,
    onPrimaryContainer = Color.White,
    secondary = GazetteSecondaryVariant,
    onSecondary = Color.Black,
    secondaryContainer = GazetteCardBorderDark,
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF34D399),
    background = GazetteBgDark,
    onBackground = TextPrimaryDark,
    surface = GazetteSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = GazetteCardDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = GazetteCardBorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = GazettePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE2E8F0),
    onPrimaryContainer = GazettePrimary,
    secondary = GazetteSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = GazetteSecondary,
    tertiary = GazetteTertiary,
    background = GazetteBgLight,
    onBackground = TextPrimaryLight,
    surface = GazetteSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = GazetteCardLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = GazetteCardBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent branding colors for official gazette aesthetic
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
