package com.nazam.meteo.feature.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.usecase.GetWeatherUseCase
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel = prépare les données pour l’écran.
 * Pas de Compose ici.
 */
class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadDefaultCity() {
        // Paris (exemple) : on met juste une position fixe pour commencer
        loadWeather(latitude = 48.8566, longitude = 2.3522)
    }

    fun retry() {
        loadDefaultCity()
    }

    private fun loadWeather(latitude: Double, longitude: Double) {
        _uiState.value = WeatherUiState.Loading

        viewModelScope.launch {
            when (val result = getWeatherUseCase.execute(latitude, longitude)) {
                is AppResult.Success -> _uiState.value = WeatherUiState.Success(result.data)
                is AppResult.Error -> _uiState.value = WeatherUiState.Error(result.error.toUiMessage())
            }
        }
    }

    private fun AppError.toUiMessage(): String {
        return when (this) {
            AppError.Network -> "Pas de connexion internet"
            AppError.NotFound -> "Ville introuvable"
            is AppError.Unknown -> "Erreur inconnue"
        }
    }
}