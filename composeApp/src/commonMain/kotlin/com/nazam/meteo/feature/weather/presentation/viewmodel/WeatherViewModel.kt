package com.nazam.meteo.feature.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.core.ui.UiText
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.usecase.GetWeatherUseCase
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import meteo.composeapp.generated.resources.Res
import meteo.composeapp.generated.resources.error_city_not_found
import meteo.composeapp.generated.resources.error_no_internet
import meteo.composeapp.generated.resources.error_unknown

class WeatherViewModel(
    private val getWeatherUseCase: GetWeatherUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadDefaultCity() {
        loadWeather(
            latitude = 48.8566,
            longitude = 2.3522,
            cityName = "Paris"
        )
    }

    fun loadCity(city: City) {
        loadWeather(
            latitude = city.latitude,
            longitude = city.longitude,
            cityName = city.displayName()
        )
    }

    fun retry() {
        loadDefaultCity()
    }

    /**
     * cityName a une valeur par défaut -> évite les soucis si un appel ancien existe.
     */
    private fun loadWeather(
        latitude: Double,
        longitude: Double,
        cityName: String = ""
    ) {
        _uiState.value = WeatherUiState.Loading

        viewModelScope.launch {
            when (val result = getWeatherUseCase.execute(latitude, longitude, cityName)) {
                is AppResult.Success -> _uiState.value = WeatherUiState.Success(result.data)
                is AppResult.Error -> _uiState.value = WeatherUiState.Error(result.error.toUiText())
            }
        }
    }

    private fun AppError.toUiText(): UiText {
        return when (this) {
            AppError.Network -> UiText.Resource(Res.string.error_no_internet)
            AppError.NotFound -> UiText.Resource(Res.string.error_city_not_found)
            is AppError.Unknown -> UiText.Resource(Res.string.error_unknown)
        }
    }
}