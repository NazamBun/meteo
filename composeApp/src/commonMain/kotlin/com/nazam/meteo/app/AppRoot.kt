package com.nazam.meteo.app

import androidx.compose.runtime.Composable
import com.nazam.meteo.feature.weather.presentation.ui.WeatherRoute

/**
 * Racine de l’application.
 * Plus tard : navigation entre Weather, Search, Favorites.
 */
@Composable
fun AppRoot() {
    WeatherRoute()
}