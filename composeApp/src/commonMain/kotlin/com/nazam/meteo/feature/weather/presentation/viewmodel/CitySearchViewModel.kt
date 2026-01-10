package com.nazam.meteo.feature.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.usecase.SearchCityUseCase
import com.nazam.meteo.feature.weather.presentation.model.CitySearchUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel de recherche.
 * Il ne charge PAS la météo, il ne fait que chercher des villes.
 */
class CitySearchViewModel(
    private val searchCityUseCase: SearchCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitySearchUiState())
    val uiState: StateFlow<CitySearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(newQuery: String) {
        _uiState.value = _uiState.value.copy(
            query = newQuery,
            errorMessage = null
        )
    }

    fun search() {
        val query = _uiState.value.query

        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            when (val result = searchCityUseCase.execute(query)) {
                is AppResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        results = result.data,
                        errorMessage = null
                    )
                }

                is AppResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        results = emptyList(),
                        errorMessage = result.error.toUiMessage()
                    )
                }
            }
        }
    }

    fun clearResults() {
        _uiState.value = _uiState.value.copy(
            results = emptyList(),
            errorMessage = null
        )
    }

    private fun AppError.toUiMessage(): String {
        return when (this) {
            AppError.Network -> "Pas de connexion internet"
            AppError.NotFound -> "Ville introuvable"
            is AppError.Unknown -> "Erreur inconnue"
        }
    }
}