package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.meteo.feature.weather.domain.model.DailyForecast
import com.nazam.meteo.feature.weather.domain.model.HourlyForecast
import com.nazam.meteo.feature.weather.domain.model.Weather
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState

/**
 * Écran météo : simple, moderne, et sans icônes.
 * KMP-friendly (que du Compose multiplatform).
 */
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
            is WeatherUiState.Error -> ErrorContent(
                message = uiState.message,
                onRetry = onRetry
            )
            is WeatherUiState.Success -> WeatherContent(weather = uiState.weather)
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Chargement...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text(text = "Réessayer")
        }
    }
}

@Composable
private fun WeatherContent(weather: Weather) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        // ✅ Header
        HeaderCard(
            city = weather.city,
            temperatureC = weather.temperatureC,
            description = weather.description
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Hourly (horizontal)
        SectionTitle(title = "Heure par heure")
        Spacer(modifier = Modifier.height(8.dp))
        HourlyRow(items = weather.hourly)

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Daily (vertical)
        SectionTitle(title = "Prochains jours")
        Spacer(modifier = Modifier.height(8.dp))
        DailyColumn(items = weather.daily)
    }
}

@Composable
private fun HeaderCard(
    city: String,
    temperatureC: Int,
    description: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            .padding(16.dp)
    ) {
        Text(
            text = city,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "$temperatureC°C",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
private fun HourlyRow(items: List<HourlyForecast>) {
    if (items.isEmpty()) {
        Text(text = "Aucune donnée", style = MaterialTheme.typography.bodyMedium)
        return
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(items) { hour ->
            HourlyItem(item = hour)
        }
    }
}

@Composable
private fun HourlyItem(item: HourlyForecast) {
    Column(
        modifier = Modifier
            .width(88.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = item.hour,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "${item.temperatureC}°",
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun DailyColumn(items: List<DailyForecast>) {
    if (items.isEmpty()) {
        Text(text = "Aucune donnée", style = MaterialTheme.typography.bodyMedium)
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items.forEach { day ->
            DailyItem(item = day)
        }
    }
}

@Composable
private fun DailyItem(item: DailyForecast) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.day,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "${item.maxC}°",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${item.minC}°",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}