package com.nazam.meteo.feature.weather.domain.model

/**
 * Modèle métier principal : la météo.
 * Pas de JSON ici. Pas de DTO. Juste du propre.
 */
data class Weather(
    val city: String,
    val temperatureC: Int,
    val description: String,

    // ✅ Important pour l’UX : on garde le code météo brut
    // Ça permet de choisir une icône fiable côté UI
    val weatherCode: Int,

    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>
)