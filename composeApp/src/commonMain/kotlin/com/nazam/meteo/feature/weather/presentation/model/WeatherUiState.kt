package com.nazam.meteo.feature.weather.presentation.model

import com.nazam.meteo.feature.weather.domain.model.Weather

/**
 * Etat UI simple :
 * - Loading : on charge
 * - Success : on a la météo
 * - Error : on affiche un message
 */
sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val weather: Weather) : WeatherUiState()
    data class Error(val message: String) : WeatherUiState()
}