package com.nazam.meteo.feature.weather.presentation.model

import com.nazam.meteo.core.ui.UiText
import com.nazam.meteo.feature.weather.domain.model.Weather

sealed class WeatherUiState {
    data object Loading : WeatherUiState()
    data class Success(val weather: Weather) : WeatherUiState()
    data class Error(val message: UiText) : WeatherUiState()
}