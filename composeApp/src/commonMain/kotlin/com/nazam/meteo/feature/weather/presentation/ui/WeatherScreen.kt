package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.presentation.model.CitySearchUiState
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState

@Composable
fun WeatherScreen(
    weatherUiState: WeatherUiState,
    searchUiState: CitySearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCitySelected: (City) -> Unit,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SearchSection(
                uiState = searchUiState,
                onQueryChange = onSearchQueryChange,
                onSearchClick = onSearchClick,
                onCitySelected = onCitySelected
            )

            when (weatherUiState) {
                WeatherUiState.Loading -> LoadingContent()
                is WeatherUiState.Error -> ErrorContent(message = weatherUiState.message, onRetry = onRetry)
                is WeatherUiState.Success -> SuccessContent(
                    city = weatherUiState.weather.city,
                    temp = weatherUiState.weather.temperatureC,
                    description = weatherUiState.weather.description
                )
            }
        }
    }
}

@Composable
private fun SearchSection(
    uiState: CitySearchUiState,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCitySelected: (City) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Rechercher une ville",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = uiState.query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = { Text("Ex: Paris") }
        )

        Button(
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isLoading
        ) {
            Text(if (uiState.isLoading) "Recherche..." else "Chercher")
        }

        uiState.errorMessage?.let { msg ->
            Text(text = msg, style = MaterialTheme.typography.bodyMedium)
        }

        if (uiState.results.isNotEmpty()) {
            Text(
                text = "Résultats",
                style = MaterialTheme.typography.titleSmall
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(uiState.results) { city ->
                    CityRow(
                        city = city,
                        onClick = { onCitySelected(city) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CityRow(
    city: City,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = city.displayName(), style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "Lat ${city.latitude}, Lon ${city.longitude}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Text(text = "Chargement...", style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onRetry) {
            Text(text = "Réessayer")
        }
    }
}

@Composable
private fun SuccessContent(
    city: String,
    temp: Int,
    description: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = city, style = MaterialTheme.typography.headlineMedium)
        Text(text = "$temp C", style = MaterialTheme.typography.displaySmall)
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
    }
}