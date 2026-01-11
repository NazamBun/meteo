package com.nazam.meteo.feature.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.core.ui.AppStrings
import com.nazam.meteo.core.ui.UiText
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.usecase.GetWeatherUseCase
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    private fun loadWeather(
        latitude: Double,
        longitude: Double,
        cityName: String = ""
    ) {
        _uiState.value = WeatherUiState.Loading

        viewModelScope.launch {
            when (val result = getWeatherUseCase.execute(
                latitude = latitude,
                longitude = longitude,
                cityName = cityName
            )) {
                is AppResult.Success -> {
                    val finalCity = if (cityName.isBlank()) result.data.city else cityName
                    val weather = result.data.copy(city = finalCity)
                    _uiState.value = WeatherUiState.Success(weather)
                }

                is AppResult.Error -> {
                    _uiState.value = WeatherUiState.Error(result.error.toUiText())
                }
            }
        }
    }

    private fun AppError.toUiText(): UiText {
        return when (this) {
            AppError.Network -> UiText.StringKey(AppStrings.Key.ErrorNoInternet)
            AppError.NotFound -> UiText.StringKey(AppStrings.Key.ErrorCityNotFound)
            is AppError.Unknown -> UiText.StringKey(AppStrings.Key.ErrorUnknown)
        }
    }
}