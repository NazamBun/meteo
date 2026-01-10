package com.nazam.meteo.feature.weather.domain.repository

import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.Weather

/**
 * Contrat : le domain veut la météo pour une position + un nom lisible.
 */
interface WeatherRepository {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        cityName: String
    ): AppResult<Weather>
}