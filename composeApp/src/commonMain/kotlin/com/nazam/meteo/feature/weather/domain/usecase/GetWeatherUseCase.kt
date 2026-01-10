package com.nazam.meteo.feature.weather.domain.usecase

import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.Weather
import com.nazam.meteo.feature.weather.domain.repository.WeatherRepository

/**
 * UseCase = récupérer la météo.
 */
class GetWeatherUseCase(
    private val repository: WeatherRepository
) {
    suspend fun execute(
        latitude: Double,
        longitude: Double,
        cityName: String
    ): AppResult<Weather> {
        return repository.getWeather(latitude, longitude, cityName)
    }
}