package com.nazam.meteo.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color

/**
 * Theme Material 3 simple et KMP friendly.
 *
 * IMPORTANT :
 * - On garde un thème "neutre" (light/dark)
 * - Le vrai look "météo Apple" vient surtout du fond dégradé dans WeatherScreen.
 */
@Composable
fun MeteoTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val lightColors = lightColorScheme(
        primary = MeteoColors.SkyBlue,
        secondary = MeteoColors.SunYellow,
        background = MeteoColors.White,
        surface = MeteoColors.White,
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black
    )

    val darkColors = darkColorScheme(
        primary = MeteoColors.DeepBlue,
        secondary = MeteoColors.SunYellow,
        background = MeteoColors.DeepBlue,
        surface = MeteoColors.DarkGray,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
        content = content
    )
}

/**
 * Ta palette, en dur, simple.
 */
object MeteoColors {
    val SkyBlue = Color(0xFF4FC3F7)   // Bleu ciel
    val DeepBlue = Color(0xFF1565C0)  // Bleu foncé
    val SunYellow = Color(0xFFFFD54F) // Jaune soleil
    val LightGray = Color(0xFFECEFF1) // Gris clair
    val DarkGray = Color(0xFF455A64)  // Gris foncé
    val White = Color(0xFFFFFFFF)     // Blanc

    // Bonus (orage) : bleu très foncé (simple)
    val StormBlue = Color(0xFF0D1B2A)
}