package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.nazam.meteo.core.network.createHttpClient
import com.nazam.meteo.feature.weather.data.remote.OpenMeteoApi
import com.nazam.meteo.feature.weather.data.remote.OpenMeteoGeocodingApi
import com.nazam.meteo.feature.weather.data.repository.CityRepositoryImpl
import com.nazam.meteo.feature.weather.data.repository.WeatherRepositoryImpl
import com.nazam.meteo.feature.weather.domain.usecase.GetWeatherUseCase
import com.nazam.meteo.feature.weather.domain.usecase.SearchCityUseCase
import com.nazam.meteo.feature.weather.presentation.viewmodel.CitySearchViewModel
import com.nazam.meteo.feature.weather.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherRoute() {
    // Pas de DI : on branche à la main (simple et clair).

    val client = remember { createHttpClient() }

    // Weather API
    val weatherApi = remember { OpenMeteoApi(client) }
    val weatherRepository = remember { WeatherRepositoryImpl(weatherApi) }
    val getWeatherUseCase = remember { GetWeatherUseCase(weatherRepository) }
    val weatherViewModel = remember { WeatherViewModel(getWeatherUseCase) }

    // Geocoding API
    val geocodingApi = remember { OpenMeteoGeocodingApi(client) }
    val cityRepository = remember { CityRepositoryImpl(geocodingApi) }
    val searchCityUseCase = remember { SearchCityUseCase(cityRepository) }
    val citySearchViewModel = remember { CitySearchViewModel(searchCityUseCase) }

    val weatherUiState by weatherViewModel.uiState.collectAsState()
    val searchUiState by citySearchViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        weatherViewModel.loadDefaultCity()
    }

    WeatherScreen(
        weatherUiState = weatherUiState,
        searchUiState = searchUiState,
        onSearchQueryChange = { citySearchViewModel.onQueryChange(it) },
        onSearchClick = { citySearchViewModel.search() },
        onCitySelected = { city ->
            // Quand l'utilisateur choisit une ville → on charge la météo
            weatherViewModel.loadCity(city)
            citySearchViewModel.clearResults()
        },
        onRetry = { weatherViewModel.retry() }
    )
}