package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState

@Composable
fun WeatherScreen(
    uiState: WeatherUiState,
    onRetry: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (uiState) {
            WeatherUiState.Loading -> LoadingContent()
            is WeatherUiState.Error -> ErrorContent(message = uiState.message, onRetry = onRetry)
            is WeatherUiState.Success -> SuccessContent(
                city = uiState.weather.city,
                temp = uiState.weather.temperatureC,
                description = uiState.weather.description
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Chargement...", style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
            Text(text = "Reessayer")
        }
    }
}

@Composable
private fun SuccessContent(
    city: String,
    temp: Int,
    description: String
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = city, style = MaterialTheme.typography.headlineMedium)
        Text(text = "$temp C", style = MaterialTheme.typography.displaySmall)
        Text(text = description, style = MaterialTheme.typography.bodyLarge)
    }
}