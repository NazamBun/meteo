package com.nazam.meteo.feature.weather.data.repository

import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.DailyForecast
import com.nazam.meteo.feature.weather.domain.model.HourlyForecast
import com.nazam.meteo.feature.weather.domain.model.Weather
import com.nazam.meteo.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.delay

/**
 * Implémentation DATA.
 * Pour l’instant : fake data (pas de réseau).
 * Plus tard : on branchera Open-Meteo ici.
 */
class WeatherRepositoryImpl : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double): AppResult<Weather> {
        // Simule un petit délai comme une vraie API
        delay(400)

        val weather = Weather(
            city = "Paris",
            temperatureC = 18,
            description = "Temps calme",
            hourly = listOf(
                HourlyForecast(hour = "10:00", temperatureC = 17),
                HourlyForecast(hour = "12:00", temperatureC = 18),
                HourlyForecast(hour = "14:00", temperatureC = 19),
                HourlyForecast(hour = "16:00", temperatureC = 18),
            ),
            daily = listOf(
                DailyForecast(day = "Lun", maxC = 21, minC = 12),
                DailyForecast(day = "Mar", maxC = 19, minC = 11),
                DailyForecast(day = "Mer", maxC = 20, minC = 13),
            )
        )

        return AppResult.Success(weather)
    }
}