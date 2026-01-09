package com.nazam.meteo.feature.weather.domain.model

data class HourlyForecast(
    val hour: String,
    val temperatureC: Int
)