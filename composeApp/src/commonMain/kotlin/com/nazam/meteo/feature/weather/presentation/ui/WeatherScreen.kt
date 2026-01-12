package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material.icons.rounded.Umbrella
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nazam.meteo.core.ui.theme.MeteoColors
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.model.DailyForecast
import com.nazam.meteo.feature.weather.domain.model.HourlyForecast
import com.nazam.meteo.feature.weather.domain.model.Weather
import com.nazam.meteo.feature.weather.presentation.model.CitySearchUiState
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun WeatherScreen(
    weatherUiState: WeatherUiState,
    searchUiState: CitySearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCitySelected: (City) -> Unit,
    onRetry: () -> Unit
) {
    val visual = when (weatherUiState) {
        is WeatherUiState.Success -> weatherVisualFromDescription(weatherUiState.weather.description)
        else -> WeatherVisual.Default
    }

    val backgroundBrush = backgroundGradientFor(visual)
    val contentColor = contentColorFor(visual)

    // Texte + icônes lisibles partout
    CompositionLocalProvider(LocalContentColor provides contentColor) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Header()
            }

            item {
                GlassCard(visual = visual) {
                    SearchCardContent(
                        uiState = searchUiState,
                        onQueryChange = onSearchQueryChange,
                        onSearchClick = onSearchClick,
                        onCitySelected = onCitySelected
                    )
                }
            }

            when (weatherUiState) {
                WeatherUiState.Loading -> {
                    item {
                        GlassCard(visual = visual) { LoadingContent() }
                    }
                }

                is WeatherUiState.Error -> {
                    item {
                        GlassCard(visual = visual) {
                            ErrorContent(
                                message = weatherUiState.message.asString(),
                                onRetry = onRetry
                            )
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    // Carte principale (grosse, mise en avant)
                    item {
                        GlassCard(visual = visual) {
                            MainWeatherCard(
                                weather = weatherUiState.weather,
                                visual = visual
                            )
                        }
                    }

                    // Heure par heure
                    if (weatherUiState.weather.hourly.isNotEmpty()) {
                        item {
                            GlassCard(visual = visual) { HourlyRow(hourly = weatherUiState.weather.hourly) }
                        }
                    }

                    // Prévisions
                    if (weatherUiState.weather.daily.isNotEmpty()) {
                        item {
                            GlassCard(visual = visual) { DailyList(daily = weatherUiState.weather.daily) }
                        }
                    }
                }
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
            style = MaterialTheme.typography.bodyMedium,
            color = LocalContentColor.current.copy(alpha = 0.85f)
        )
    }
}

@Composable
private fun GlassCard(
    visual: WeatherVisual,
    content: @Composable () -> Unit
) {
    val isDark = isDarkBackground(visual)

    val cardColor = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.55f)
    }

    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.18f)
    } else {
        Color.White.copy(alpha = 0.25f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchCardContent(
    uiState: CitySearchUiState,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onCitySelected: (City) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Rounded.Search, contentDescription = null)
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(Res.string.search_title),
            style = MaterialTheme.typography.titleMedium
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

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

    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = onSearchClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = !uiState.isLoading && uiState.query.isNotBlank()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Rounded.Search, contentDescription = null, modifier = Modifier.size(18.dp))
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
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = msg.asString(), style = MaterialTheme.typography.bodyMedium)
    }

    // IMPORTANT : pas de LazyColumn ici (sinon scroll dans scroll)
    if (uiState.results.isNotEmpty()) {
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = stringResource(Res.string.results_title), style = MaterialTheme.typography.titleSmall)

        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            uiState.results.take(8).forEach { city ->
                CityRow(city = city, onClick = { onCitySelected(city) })
            }
        }
    }
}

@Composable
private fun CityRow(
    city: City,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Rounded.LocationOn, contentDescription = null, modifier = Modifier.size(22.dp))
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
                color = LocalContentColor.current.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LoadingContent() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Rounded.CloudQueue, contentDescription = null)
        Spacer(modifier = Modifier.size(10.dp))
        Text(text = stringResource(Res.string.loading), style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)

        Button(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = stringResource(Res.string.retry))
            }
        }
    }
}

/**
 * Carte principale :
 * - Icône météo dynamique (grosse)
 * - Temp très visible
 * - Ville + description bien mises en avant
 */
@Composable
private fun MainWeatherCard(
    weather: Weather,
    visual: WeatherVisual
) {
    val icon = mainWeatherIconFor(visual, weather.weatherCode)

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Ligne du haut : ville
        Text(
            text = weather.city,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Ligne centrale : grosse icône + grosse température
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(72.dp) // ✅ plus grand
            )

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                Text(
                    text = stringResource(Res.string.temp_c, weather.temperatureC),
                    style = MaterialTheme.typography.displayMedium // ✅ plus gros
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = smallWeatherIconFor(visual, weather.weatherCode),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = weather.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = LocalContentColor.current.copy(alpha = 0.90f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Petit détail “thermostat” (optionnel mais sympa)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Rounded.Thermostat, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Ressenti proche de la température",
                style = MaterialTheme.typography.bodySmall,
                color = LocalContentColor.current.copy(alpha = 0.80f)
            )
        }
    }
}

@Composable
private fun HourlyRow(hourly: List<HourlyForecast>) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = Icons.Rounded.Schedule, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = stringResource(Res.string.hourly_title), style = MaterialTheme.typography.titleMedium)
    }

    Spacer(modifier = Modifier.height(10.dp))

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(hourly.take(24)) { item ->
            HourChip(hour = item.hour, temp = item.temperatureC)
        }
    }
}

@Composable
private fun HourChip(hour: String, temp: Int) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.20f)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
    Text(text = stringResource(Res.string.daily_title), style = MaterialTheme.typography.titleMedium)

    Spacer(modifier = Modifier.height(10.dp))

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        daily.take(7).forEach { item ->
            DailyRow(item)
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

        Text(
            text = stringResource(Res.string.min_temp_c, item.minC),
            style = MaterialTheme.typography.bodySmall,
            color = LocalContentColor.current.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = stringResource(Res.string.max_temp_c, item.maxC),
            style = MaterialTheme.typography.bodySmall,
            color = LocalContentColor.current.copy(alpha = 0.85f)
        )
    }
}

/* ---------------------------
   Icônes météo dynamiques
   --------------------------- */

private fun mainWeatherIconFor(visual: WeatherVisual, weatherCode: Int): ImageVector {
    // Priorité au code météo (plus fiable), sinon on utilise le visual
    return when {
        isThunderstormCode(weatherCode) -> Icons.Rounded.Bolt
        isSnowCode(weatherCode) -> Icons.Rounded.AcUnit
        isRainCode(weatherCode) -> Icons.Rounded.Umbrella
        isCloudyCode(weatherCode) -> Icons.Rounded.Cloud
        isSunnyCode(weatherCode) -> Icons.Rounded.WbSunny
        else -> when (visual) {
            WeatherVisual.Sunny -> Icons.Rounded.WbSunny
            WeatherVisual.Cloudy -> Icons.Rounded.Cloud
            WeatherVisual.Rainy -> Icons.Rounded.Umbrella
            WeatherVisual.Stormy -> Icons.Rounded.Bolt
            WeatherVisual.Snowy -> Icons.Rounded.AcUnit
            WeatherVisual.Default -> Icons.Rounded.Cloud
        }
    }
}

private fun smallWeatherIconFor(visual: WeatherVisual, weatherCode: Int): ImageVector {
    // Version “petite” (tu peux changer si tu veux un autre style)
    return mainWeatherIconFor(visual, weatherCode)
}

// Open-Meteo weather code (simplifié)
private fun isSunnyCode(code: Int): Boolean = code == 0
private fun isCloudyCode(code: Int): Boolean = code in listOf(1, 2, 3, 45, 48)
private fun isRainCode(code: Int): Boolean = code in 51..67 || code in 80..82
private fun isSnowCode(code: Int): Boolean = code in 71..77 || code in 85..86
private fun isThunderstormCode(code: Int): Boolean = code in 95..99

/* ---------------------------
   Style météo (Apple-like)
   --------------------------- */

private enum class WeatherVisual {
    Sunny,
    Cloudy,
    Rainy,
    Stormy,
    Snowy,
    Default
}

private fun weatherVisualFromDescription(description: String): WeatherVisual {
    val d = description.lowercase()

    return when {
        d.contains("clair") || d.contains("soleil") -> WeatherVisual.Sunny
        d.contains("nuage") || d.contains("couvert") || d.contains("brouillard") -> WeatherVisual.Cloudy
        d.contains("pluie") || d.contains("bruine") -> WeatherVisual.Rainy
        d.contains("orage") -> WeatherVisual.Stormy
        d.contains("neige") -> WeatherVisual.Snowy
        else -> WeatherVisual.Default
    }
}

private fun isDarkBackground(visual: WeatherVisual): Boolean {
    return when (visual) {
        WeatherVisual.Rainy, WeatherVisual.Stormy -> true
        else -> false
    }
}

private fun contentColorFor(visual: WeatherVisual): Color {
    return if (isDarkBackground(visual)) Color.White else Color.Black
}

@Composable
private fun backgroundGradientFor(visual: WeatherVisual): Brush {
    return when (visual) {
        WeatherVisual.Sunny -> Brush.verticalGradient(
            colors = listOf(
                MeteoColors.SkyBlue,
                MeteoColors.SkyBlue,
                MeteoColors.SunYellow
            )
        )

        WeatherVisual.Cloudy -> Brush.verticalGradient(
            colors = listOf(
                MeteoColors.LightGray,
                MeteoColors.SkyBlue.copy(alpha = 0.65f)
            )
        )

        WeatherVisual.Rainy -> Brush.verticalGradient(
            colors = listOf(
                MeteoColors.DeepBlue,
                MeteoColors.DarkGray
            )
        )

        WeatherVisual.Stormy -> Brush.verticalGradient(
            colors = listOf(
                MeteoColors.StormBlue,
                MeteoColors.DarkGray
            )
        )

        WeatherVisual.Snowy -> Brush.verticalGradient(
            colors = listOf(
                MeteoColors.White,
                MeteoColors.SkyBlue.copy(alpha = 0.35f)
            )
        )

        WeatherVisual.Default -> Brush.verticalGradient(
            colors = listOf(
                MeteoColors.SkyBlue.copy(alpha = 0.70f),
                MeteoColors.LightGray
            )
        )
    }
}