package com.nazam.meteo.feature.weather.presentation.model

import com.nazam.meteo.feature.weather.domain.model.City

/**
 * Etat pour la recherche de ville.
 */
data class CitySearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<City> = emptyList(),
    val errorMessage: String? = null
)