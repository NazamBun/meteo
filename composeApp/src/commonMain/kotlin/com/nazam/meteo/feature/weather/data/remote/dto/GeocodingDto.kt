package com.nazam.meteo.feature.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO = format JSON exact reçu par l'API.
 */
@Serializable
data class GeocodingResponseDto(
    @SerialName("results")
    val results: List<GeocodingCityDto>? = null
)

@Serializable
data class GeocodingCityDto(
    @SerialName("name")
    val name: String? = null,

    @SerialName("country")
    val country: String? = null,

    @SerialName("admin1")
    val admin1: String? = null,

    @SerialName("latitude")
    val latitude: Double? = null,

    @SerialName("longitude")
    val longitude: Double? = null
)