package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.nazam.meteo.feature.weather.data.repository.WeatherRepositoryImpl
import com.nazam.meteo.feature.weather.domain.usecase.GetWeatherUseCase
import com.nazam.meteo.feature.weather.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherRoute() {
    // Pas de DI pour l’instant : on branche à la main (simple et propre).
    val repository = remember { WeatherRepositoryImpl() }
    val useCase = remember { GetWeatherUseCase(repository) }
    val viewModel = remember { WeatherViewModel(useCase) }

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDefaultCity()
    }

    WeatherScreen(
        uiState = uiState,
        onRetry = { viewModel.retry() }
    )
}