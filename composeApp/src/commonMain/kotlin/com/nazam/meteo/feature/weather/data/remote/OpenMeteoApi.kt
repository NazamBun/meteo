package com.nazam.meteo.feature.weather.data.remote

import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.data.remote.dto.WeatherDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * Petit client réseau dédié à Open-Meteo.
 * Il ne connaît PAS le domain, il ne renvoie que des DTO.
 */
class OpenMeteoApi(
    private val client: HttpClient
) {
    suspend fun fetchWeather(latitude: Double, longitude: Double): AppResult<WeatherDto> {
        return try {
            val dto: WeatherDto = client.get("https://api.open-meteo.com/v1/forecast") {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter("current", "temperature_2m,weather_code")
                parameter("hourly", "temperature_2m")
                parameter("daily", "temperature_2m_max,temperature_2m_min")
                parameter("timezone", "auto")
            }.body()

            AppResult.Success(dto)
        } catch (t: Throwable) {
            // simple : on classe tout en erreur réseau pour l'instant
            AppResult.Error(AppError.Network)
        }
    }
}