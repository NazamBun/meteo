package com.nazam.meteo.feature.weather.data.remote.mapper

import com.nazam.meteo.feature.weather.data.remote.dto.WeatherDto
import com.nazam.meteo.feature.weather.domain.model.DailyForecast
import com.nazam.meteo.feature.weather.domain.model.HourlyForecast
import com.nazam.meteo.feature.weather.domain.model.Weather
import kotlin.math.roundToInt

/**
 * Mapper : DTO (data) -> Model (domain)
 * Ça protège le domain, il ne dépend jamais du JSON.
 */
object WeatherMapper {

    fun toDomain(dto: WeatherDto, city: String): Weather {
        val tempC = dto.current?.temperature2m?.roundToInt() ?: 0
        val code = dto.current?.weatherCode ?: -1

        val hourly = dto.hourly?.let { h ->
            val size = minOf(h.time.size, h.temperature2m.size)
            (0 until size).map { index ->
                HourlyForecast(
                    hour = h.time[index].takeLast(5), // "HH:mm"
                    temperatureC = h.temperature2m[index].roundToInt()
                )
            }
        } ?: emptyList()

        val daily = dto.daily?.let { d ->
            val size = minOf(d.time.size, d.max.size, d.min.size)
            (0 until size).map { index ->
                DailyForecast(
                    day = d.time[index],
                    maxC = d.max[index].roundToInt(),
                    minC = d.min[index].roundToInt()
                )
            }
        } ?: emptyList()

        return Weather(
            city = city,
            temperatureC = tempC,
            description = codeToText(code),

            // ✅ nouveau champ
            weatherCode = code,

            hourly = hourly,
            daily = daily
        )
    }

    private fun codeToText(code: Int): String {
        return when (code) {
            0 -> "Ciel clair"
            1, 2, 3 -> "Nuageux"
            45, 48 -> "Brouillard"
            51, 53, 55 -> "Bruine"
            61, 63, 65 -> "Pluie"
            71, 73, 75 -> "Neige"
            95 -> "Orage"
            else -> "Inconnu"
        }
    }
}