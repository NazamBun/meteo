package com.nazam.meteo.feature.weather.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nazam.meteo.core.result.AppError
import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.core.ui.UiText
import com.nazam.meteo.feature.weather.domain.usecase.SearchCityUseCase
import com.nazam.meteo.feature.weather.presentation.model.CitySearchUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import meteo.composeapp.generated.resources.Res
import meteo.composeapp.generated.resources.error_city_not_found
import meteo.composeapp.generated.resources.error_no_internet
import meteo.composeapp.generated.resources.error_unknown

/**
 * ViewModel de recherche de ville.
 *
 * UX :
 * - auto-complétion quand l'utilisateur tape
 * - debounce pour éviter trop d'appels réseau
 */
class CitySearchViewModel(
    private val searchCityUseCase: SearchCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitySearchUiState())
    val uiState: StateFlow<CitySearchUiState> = _uiState.asStateFlow()

    init {
        observeQueryForAutoComplete()
    }

    fun onQueryChange(value: String) {
        _uiState.update {
            it.copy(
                query = value,
                errorMessage = null
            )
        }
    }

    fun clearResults() {
        _uiState.update {
            it.copy(
                isLoading = false,
                results = emptyList(),
                errorMessage = null
            )
        }
    }

    fun search() {
        val query = _uiState.value.query.trim()
        if (query.isBlank()) return

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = searchCityUseCase.execute(query)) {
                is AppResult.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = result.data,
                            errorMessage = null
                        )
                    }
                }

                is AppResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            results = emptyList(),
                            errorMessage = result.error.toUiText()
                        )
                    }
                }
            }
        }
    }

    @OptIn(FlowPreview::class) // ✅ pour debounce()
    private fun observeQueryForAutoComplete() {
        viewModelScope.launch {
            _uiState
                .map { it.query.trim() }
                .distinctUntilChanged()
                .debounce(350)
                .collect { query ->

                    if (query.isBlank()) {
                        clearResults()
                        return@collect
                    }

                    if (query.length < 2) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                results = emptyList(),
                                errorMessage = null
                            )
                        }
                        return@collect
                    }

                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                    when (val result = searchCityUseCase.execute(query)) {
                        is AppResult.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    results = result.data,
                                    errorMessage = null
                                )
                            }
                        }

                        is AppResult.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    results = emptyList(),
                                    errorMessage = result.error.toUiText()
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun AppError.toUiText(): UiText {
        return when (this) {
            AppError.Network -> UiText.Resource(Res.string.error_no_internet)
            AppError.NotFound -> UiText.Resource(Res.string.error_city_not_found)
            is AppError.Unknown -> UiText.Resource(Res.string.error_unknown)
        }
    }
}