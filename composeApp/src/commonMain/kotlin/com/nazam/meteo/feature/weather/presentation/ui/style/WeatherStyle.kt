package com.nazam.meteo.feature.weather.presentation.ui.style

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.nazam.meteo.core.ui.theme.MeteoColors

enum class WeatherVisual {
    Sunny,
    Cloudy,
    Rainy,
    Stormy,
    Snowy,
    Default
}

data class WeatherPalette(
    val c1: Color,
    val c2: Color,
    val c3: Color
)

fun weatherVisualFromDescription(description: String): WeatherVisual {
    val d = description.lowercase()
    return when {
        d.contains("clair") || d.contains("soleil") -> WeatherVisual.Sunny
        d.contains("nuage") || d.contains("couvert") || d.contains("brouillard") -> WeatherVisual.Cloudy
        d.contains("pluie") || d.contains("bruine") -> WeatherVisual.Rainy
        d.contains("orage") -> WeatherVisual.Stormy
        d.contains("neige") -> WeatherVisual.Snowy
        else -> WeatherVisual.Default
    }
}

fun paletteFor(visual: WeatherVisual): WeatherPalette {
    return when (visual) {
        WeatherVisual.Sunny -> WeatherPalette(MeteoColors.SkyBlue, MeteoColors.SkyBlue, MeteoColors.SunYellow)
        WeatherVisual.Cloudy -> WeatherPalette(MeteoColors.LightGray, MeteoColors.SkyBlue.copy(alpha = 0.65f), MeteoColors.White)
        WeatherVisual.Rainy -> WeatherPalette(MeteoColors.DeepBlue, MeteoColors.DarkGray, MeteoColors.DeepBlue)
        WeatherVisual.Stormy -> WeatherPalette(MeteoColors.StormBlue, MeteoColors.DarkGray, MeteoColors.DeepBlue)
        WeatherVisual.Snowy -> WeatherPalette(MeteoColors.White, MeteoColors.SkyBlue.copy(alpha = 0.35f), MeteoColors.LightGray)
        WeatherVisual.Default -> WeatherPalette(MeteoColors.SkyBlue, MeteoColors.LightGray, MeteoColors.White)
    }
}

fun isDarkBackground(visual: WeatherVisual): Boolean {
    return visual == WeatherVisual.Rainy || visual == WeatherVisual.Stormy
}

fun contentColorFor(visual: WeatherVisual): Color {
    return if (isDarkBackground(visual)) Color.White else Color.Black
}

fun backgroundBrushFor(palette: WeatherPalette, progress: Float): Brush {
    val p = progress.coerceIn(0f, 1f)
    val radius = 1400f + (1f - p) * 200f

    return Brush.radialGradient(
        colors = listOf(
            palette.c3.copy(alpha = 0.98f),
            palette.c2.copy(alpha = 0.95f),
            palette.c1.copy(alpha = 1.00f)
        ),
        center = Offset.Unspecified,
        radius = radius
    )
}

fun haloColorFor(visual: WeatherVisual): Color {
    return when (visual) {
        WeatherVisual.Sunny -> MeteoColors.SunYellow
        WeatherVisual.Cloudy -> MeteoColors.SkyBlue
        WeatherVisual.Rainy -> MeteoColors.SkyBlue
        WeatherVisual.Stormy -> MeteoColors.SunYellow
        WeatherVisual.Snowy -> MeteoColors.SkyBlue
        WeatherVisual.Default -> MeteoColors.SkyBlue
    }
}