package com.nazam.meteo.feature.weather.domain.usecase

import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.repository.CityRepository

/**
 * UseCase = action métier.
 * Ici : chercher une ville à partir d'un texte.
 */
class SearchCityUseCase(
    private val repository: CityRepository
) {
    suspend fun execute(query: String): AppResult<List<City>> {
        val cleaned = query.trim()

        // Si l'utilisateur n'a rien tapé, on ne fait pas de réseau
        if (cleaned.isEmpty()) {
            return AppResult.Success(emptyList())
        }

        // Petit minimum pour éviter trop d'appels inutiles
        if (cleaned.length < 2) {
            return AppResult.Success(emptyList())
        }

        return repository.searchCity(cleaned)
    }
}