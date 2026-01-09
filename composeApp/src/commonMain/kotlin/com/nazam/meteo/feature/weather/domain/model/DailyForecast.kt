package com.nazam.meteo.feature.weather.domain.model

data class DailyForecast(
    val day: String,
    val maxC: Int,
    val minC: Int
)