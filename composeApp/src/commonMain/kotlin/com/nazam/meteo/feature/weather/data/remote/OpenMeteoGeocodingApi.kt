package com.nazam.meteo.feature.weather.data.remote

import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.data.remote.dto.GeocodingResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

/**
 * API Geocoding Open-Meteo :
 * https://geocoding-api.open-meteo.com/v1/search?name=paris
 *
 * Ce fichier renvoie uniquement des DTO.
 */
class OpenMeteoGeocodingApi(
    private val client: HttpClient
) {
    suspend fun searchCity(name: String): AppResult<GeocodingResponseDto> {
        return try {
            val dto: GeocodingResponseDto = client.get("https://geocoding-api.open-meteo.com/v1/search") {
                parameter("name", name)
                parameter("count", 8)
                parameter("language", "fr")
                parameter("format", "json")
            }.body()

            AppResult.Success(dto)
        } catch (t: Throwable) {
            AppResult.Error(AppError.Network)
        }
    }
}
