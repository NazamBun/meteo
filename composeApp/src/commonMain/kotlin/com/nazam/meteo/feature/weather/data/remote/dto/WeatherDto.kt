package com.nazam.meteo.feature.weather.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO = format exact reçu depuis l'API (JSON).
 * On garde DTO dans "data", pas dans "domain".
 */
@Serializable
data class WeatherDto(
    @SerialName("current")
    val current: CurrentDto? = null,

    @SerialName("hourly")
    val hourly: HourlyDto? = null,

    @SerialName("daily")
    val daily: DailyDto? = null
)

@Serializable
data class CurrentDto(
    @SerialName("temperature_2m")
    val temperature2m: Double? = null,

    @SerialName("weather_code")
    val weatherCode: Int? = null
)

@Serializable
data class HourlyDto(
    @SerialName("time")
    val time: List<String> = emptyList(),

    @SerialName("temperature_2m")
    val temperature2m: List<Double> = emptyList()
)

@Serializable
data class DailyDto(
    @SerialName("time")
    val time: List<String> = emptyList(),

    @SerialName("temperature_2m_max")
    val max: List<Double> = emptyList(),

    @SerialName("temperature_2m_min")
    val min: List<Double> = emptyList()
)