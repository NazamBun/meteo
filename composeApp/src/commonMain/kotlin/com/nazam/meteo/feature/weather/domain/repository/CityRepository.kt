package com.nazam.meteo.feature.weather.domain.repository

import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.City

/**
 * Contrat : le domain veut "chercher des villes" avec un texte.
 * Le domain ne sait pas si ça vient d'internet, cache, etc.
 */
interface CityRepository {
    suspend fun searchCity(query: String): AppResult<List<City>>
}