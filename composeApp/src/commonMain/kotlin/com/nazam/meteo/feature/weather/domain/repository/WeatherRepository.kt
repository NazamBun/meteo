package com.nazam.meteo.feature.weather.domain.repository

import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.Weather

/**
 * Contrat (interface) : le domain dit ce qu’il veut,
 * sans savoir si ça vient du réseau, du cache, etc.
 */
interface WeatherRepository {
    suspend fun getWeather(latitude: Double, longitude: Double): AppResult<Weather>
}