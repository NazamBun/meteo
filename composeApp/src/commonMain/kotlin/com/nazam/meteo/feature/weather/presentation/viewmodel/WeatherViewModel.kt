package com.nazam.meteo.feature.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.usecase.GetWeatherUseCase
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel météo.
 */
class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private var lastCity: City? = null

    fun loadDefaultCity() {
        // Paris par défaut
        val paris = City(
            name = "Paris",
            country = "France",
            admin1 = null,
            latitude = 48.8566,
            longitude = 2.3522
        )
        loadCity(paris)
    }

    fun loadCity(city: City) {
        lastCity = city
        loadWeather(
            latitude = city.latitude,
            longitude = city.longitude,
            cityName = city.displayName()
        )
    }

    fun retry() {
        val city = lastCity
        if (city != null) {
            loadCity(city)
        } else {
            loadDefaultCity()
        }
    }

    private fun loadWeather(
        latitude: Double,
        longitude: Double,
        cityName: String
    ) {
        _uiState.value = WeatherUiState.Loading

        viewModelScope.launch {
            when (val result = getWeatherUseCase.execute(latitude, longitude, cityName)) {
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