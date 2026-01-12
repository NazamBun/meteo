package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.model.DailyForecast
import com.nazam.meteo.feature.weather.domain.model.HourlyForecast
import com.nazam.meteo.feature.weather.domain.model.Weather
import com.nazam.meteo.feature.weather.presentation.model.CitySearchUiState
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState
import org.jetbrains.compose.resources.stringResource
import meteo.composeapp.generated.resources.Res
import meteo.composeapp.generated.resources.app_subtitle
import meteo.composeapp.generated.resources.app_title
import meteo.composeapp.generated.resources.daily_title
import meteo.composeapp.generated.resources.hourly_title
import meteo.composeapp.generated.resources.lat_lon
import meteo.composeapp.generated.resources.loading
import meteo.composeapp.generated.resources.max_temp_c
import meteo.composeapp.generated.resources.min_temp_c
import meteo.composeapp.generated.resources.results_title
import meteo.composeapp.generated.resources.retry
import meteo.composeapp.generated.resources.search_button
import meteo.composeapp.generated.resources.search_hint
import meteo.composeapp.generated.resources.search_title
import meteo.composeapp.generated.resources.searching
import meteo.composeapp.generated.resources.temp_c

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
        // ✅ IMPORTANT :
        // windowInsetsPadding(WindowInsets.safeDrawing) ajoute un padding automatique
        // pour éviter d'être sous la status bar et sous la navigation bar.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Header()

            SearchCard(
                uiState = searchUiState,
                onQueryChange = onSearchQueryChange,
                onSearchClick = onSearchClick,
                onCitySelected = onCitySelected
            )

            when (weatherUiState) {
                WeatherUiState.Loading -> LoadingCard()
                is WeatherUiState.Error -> ErrorCard(
                    message = weatherUiState.message.asString(),
                    onRetry = onRetry
                )
                is WeatherUiState.Success -> WeatherContent(weather = weatherUiState.weather)
            }
        }
    }
}

@Composable
private fun Header() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(Res.string.app_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = stringResource(Res.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchCard(
    uiState: CitySearchUiState,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCitySelected: (City) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(Res.string.search_title),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(Res.string.search_hint)) },
                leadingIcon = { Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotBlank()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(imageVector = Icons.Rounded.Clear, contentDescription = null)
                        }
                    }
                }
            )

            Button(
                onClick = onSearchClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.query.isNotBlank()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = if (uiState.isLoading)
                            stringResource(Res.string.searching)
                        else
                            stringResource(Res.string.search_button)
                    )
                }
            }

            uiState.errorMessage?.let { msg ->
                Text(text = msg.asString(), style = MaterialTheme.typography.bodyMedium)
            }

            if (uiState.results.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.results_title),
                    style = MaterialTheme.typography.titleSmall
                )

                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.results) { city ->
                        CityRow(city = city, onClick = { onCitySelected(city) })
                    }
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
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.displayName(),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(Res.string.lat_lon, city.latitude, city.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun LoadingCard() {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Rounded.Cloud, contentDescription = null)
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = stringResource(Res.string.loading), style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
private fun ErrorCard(
    message: String,
    onRetry: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = message, style = MaterialTheme.typography.bodyLarge)

            Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = stringResource(Res.string.retry))
                }
            }
        }
    }
}

@Composable
private fun WeatherContent(weather: Weather) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MainWeatherCard(
            city = weather.city,
            temp = weather.temperatureC,
            description = weather.description
        )

        if (weather.hourly.isNotEmpty()) {
            HourlyRow(hourly = weather.hourly)
        }

        if (weather.daily.isNotEmpty()) {
            DailyList(daily = weather.daily)
        }
    }
}

@Composable
private fun MainWeatherCard(
    city: String,
    temp: Int,
    description: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = city, style = MaterialTheme.typography.headlineSmall)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Thermostat, contentDescription = null, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(Res.string.temp_c, temp), style = MaterialTheme.typography.displaySmall)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Cloud, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = description, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
private fun HourlyRow(hourly: List<HourlyForecast>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(Res.string.hourly_title), style = MaterialTheme.typography.titleMedium)
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(hourly.take(24)) { item ->
                    HourChip(hour = item.hour, temp = item.temperatureC)
                }
            }
        }
    }
}

@Composable
private fun HourChip(hour: String, temp: Int) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = hour, style = MaterialTheme.typography.labelMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = stringResource(Res.string.temp_c, temp), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun DailyList(daily: List<DailyForecast>) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(text = stringResource(Res.string.daily_title), style = MaterialTheme.typography.titleMedium)

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                daily.take(7).forEach { item ->
                    DailyRow(item)
                }
            }
        }
    }
}

@Composable
private fun DailyRow(item: DailyForecast) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = item.day,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(text = stringResource(Res.string.min_temp_c, item.minC), style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.size(12.dp))

        Text(text = stringResource(Res.string.max_temp_c, item.maxC), style = MaterialTheme.typography.bodySmall)
    }
}