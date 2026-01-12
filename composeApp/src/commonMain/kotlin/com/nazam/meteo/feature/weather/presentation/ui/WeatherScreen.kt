package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
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
import kotlin.math.absoluteValue

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

    // Petite animation globale quand la météo change (fond + effets)
    val changeProgress = remember { Animatable(1f) }
    LaunchedEffect(visual) {
        changeProgress.snapTo(0f)
        changeProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 520)
        )
    }

    val palette = paletteFor(visual)
    val backgroundBrush = radialBackgroundFor(palette, changeProgress.value)

    val contentColor = contentColorFor(visual)

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            // Effets "Apple-like" derrière (nuages / étoiles)
            WeatherBackgroundEffects(
                visual = visual,
                progress = changeProgress.value
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Header() }

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
                        item { GlassCard(visual = visual) { LoadingContent() } }
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
                        item {
                            GlassCard(visual = visual) {
                                MainWeatherCard(
                                    weather = weatherUiState.weather,
                                    visual = visual
                                )
                            }
                        }

                        if (weatherUiState.weather.hourly.isNotEmpty()) {
                            item { GlassCard(visual = visual) { HourlyRow(hourly = weatherUiState.weather.hourly) } }
                        }

                        if (weatherUiState.weather.daily.isNotEmpty()) {
                            item { GlassCard(visual = visual) { DailyList(daily = weatherUiState.weather.daily) } }
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

    val cardColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.55f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.18f) else Color.White.copy(alpha = 0.25f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
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
                text = if (uiState.isLoading) stringResource(Res.string.searching) else stringResource(Res.string.search_button)
            )
        }
    }

    uiState.errorMessage?.let { msg ->
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = msg.asString(), style = MaterialTheme.typography.bodyMedium)
    }

    // Pas de scroll dans scroll
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
 * - grosse icône dynamique
 * - halo glow puissant derrière
 * - petite animation quand l'icône change
 */
@Composable
private fun MainWeatherCard(
    weather: Weather,
    visual: WeatherVisual
) {
    val icon = mainWeatherIconFor(visual, weather.weatherCode)

    val haloColor = haloColorFor(visual)
    val haloAlpha by animateFloatAsState(
        targetValue = if (isDarkBackground(visual)) 0.95f else 0.75f,
        animationSpec = tween(450)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = weather.city,
            style = MaterialTheme.typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bloc Icône + Halo
            Box(
                modifier = Modifier.size(92.dp),
                contentAlignment = Alignment.Center
            ) {
                // Halo très fort (sans blur -> on empile 3 halos)
                GlowHalo(
                    color = haloColor,
                    alpha = haloAlpha
                )

                AnimatedContent(
                    targetState = icon,
                    transitionSpec = {
                        (fadeIn(tween(220)) + scaleIn(initialScale = 0.92f, animationSpec = spring(stiffness = Spring.StiffnessLow)))
                            .togetherWith(fadeOut(tween(160)) + scaleOut(targetScale = 1.03f, animationSpec = tween(160)))
                    },
                    label = "WeatherIconAnim"
                ) { targetIcon ->
                    Icon(
                        imageVector = targetIcon,
                        contentDescription = null,
                        modifier = Modifier.size(76.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column {
                AnimatedContent(
                    targetState = weather.temperatureC,
                    transitionSpec = {
                        (fadeIn(tween(220)) + scaleIn(initialScale = 0.98f, animationSpec = spring(stiffness = Spring.StiffnessLow)))
                            .togetherWith(fadeOut(tween(150)))
                    },
                    label = "TempAnim"
                ) { temp ->
                    Text(
                        text = stringResource(Res.string.temp_c, temp),
                        style = MaterialTheme.typography.displayMedium
                    )
                }

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
    }
}

/**
 * Halo “glow” KMP-friendly :
 * pas de blur Android, mais 3 radial gradients superposés.
 */
@Composable
private fun GlowHalo(
    color: Color,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.00f),
                        color.copy(alpha = 0.15f * alpha),
                        color.copy(alpha = 0.30f * alpha),
                        color.copy(alpha = 0.00f)
                    ),
                    center = Offset.Unspecified,
                    radius = 220f
                )
            )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.00f),
                        color.copy(alpha = 0.25f * alpha),
                        color.copy(alpha = 0.00f)
                    ),
                    center = Offset.Unspecified,
                    radius = 140f
                )
            )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.45f * alpha),
                        color.copy(alpha = 0.00f)
                    ),
                    center = Offset.Unspecified,
                    radius = 70f
                )
            )
    )
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
   Effets de fond KMP (Canvas)
   - étoiles orage
   - nuages translucides
   --------------------------- */

@Composable
private fun WeatherBackgroundEffects(
    visual: WeatherVisual,
    progress: Float
) {
    // Progress sert juste à éviter un “pop” trop dur quand on change de météo
    val alpha = (0.35f + 0.65f * progress).coerceIn(0f, 1f)

    androidx.compose.foundation.Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        // Nuages semi transparents pour cloudy/rainy/stormy
        if (visual == WeatherVisual.Cloudy || visual == WeatherVisual.Rainy || visual == WeatherVisual.Stormy) {
            val cloudColor = if (isDarkBackground(visual)) {
                Color.White.copy(alpha = 0.10f * alpha)
            } else {
                Color.White.copy(alpha = 0.18f * alpha)
            }

            // 3 gros nuages doux
            drawSoftCloud(Offset(w * 0.20f, h * 0.18f), Size(w * 0.85f, h * 0.16f), cloudColor)
            drawSoftCloud(Offset(w * 0.05f, h * 0.38f), Size(w * 0.95f, h * 0.18f), cloudColor.copy(alpha = cloudColor.alpha * 0.85f))
            drawSoftCloud(Offset(w * 0.25f, h * 0.62f), Size(w * 0.80f, h * 0.14f), cloudColor.copy(alpha = cloudColor.alpha * 0.70f))
        }

        // Etoiles : uniquement orage (petites + scintillent)
        if (visual == WeatherVisual.Stormy) {
            val starColor = MeteoColors.SunYellow.copy(alpha = 0.40f * alpha)

            // positions pseudo fixes (pas random, stable)
            val stars = listOf(
                Offset(w * 0.15f, h * 0.12f),
                Offset(w * 0.32f, h * 0.20f),
                Offset(w * 0.55f, h * 0.14f),
                Offset(w * 0.74f, h * 0.24f),
                Offset(w * 0.86f, h * 0.10f),
                Offset(w * 0.64f, h * 0.32f)
            )

            stars.forEachIndexed { index, p ->
                val pulse = (0.35f + 0.65f * ((progress + index * 0.13f) % 1f))
                val r = 2.5f + index * 0.35f
                drawCircle(
                    color = starColor.copy(alpha = starColor.alpha * pulse),
                    radius = r,
                    center = p
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSoftCloud(
    topLeft: Offset,
    size: Size,
    color: Color
) {
    // Un nuage = 3 cercles + un arrondi, très simple et joli
    val x = topLeft.x
    val y = topLeft.y
    val w = size.width
    val h = size.height

    // Base arrondie
    drawRoundRect(
        color = color,
        topLeft = Offset(x, y + h * 0.35f),
        size = Size(w, h * 0.55f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(h, h),
        style = Fill
    )

    // Bulles
    drawCircle(color = color, radius = h * 0.32f, center = Offset(x + w * 0.25f, y + h * 0.45f))
    drawCircle(color = color, radius = h * 0.40f, center = Offset(x + w * 0.45f, y + h * 0.36f))
    drawCircle(color = color, radius = h * 0.30f, center = Offset(x + w * 0.65f, y + h * 0.48f))
}

/* ---------------------------
   Icônes météo dynamiques
   --------------------------- */

private fun mainWeatherIconFor(visual: WeatherVisual, weatherCode: Int): ImageVector {
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
    return mainWeatherIconFor(visual, weatherCode)
}

// Open-Meteo weather code (simplifié)
private fun isSunnyCode(code: Int): Boolean = code == 0
private fun isCloudyCode(code: Int): Boolean = code in listOf(1, 2, 3, 45, 48)
private fun isRainCode(code: Int): Boolean = code in 51..67 || code in 80..82
private fun isSnowCode(code: Int): Boolean = code in 71..77 || code in 85..86
private fun isThunderstormCode(code: Int): Boolean = code in 95..99

/* ---------------------------
   Style météo (radial)
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

private data class WeatherPalette(
    val c1: Color,
    val c2: Color,
    val c3: Color
)

private fun paletteFor(visual: WeatherVisual): WeatherPalette {
    return when (visual) {
        WeatherVisual.Sunny -> WeatherPalette(
            c1 = MeteoColors.SkyBlue,
            c2 = MeteoColors.SkyBlue.copy(alpha = 0.85f),
            c3 = MeteoColors.SunYellow
        )

        WeatherVisual.Cloudy -> WeatherPalette(
            c1 = MeteoColors.LightGray,
            c2 = MeteoColors.SkyBlue.copy(alpha = 0.65f),
            c3 = MeteoColors.White
        )

        WeatherVisual.Rainy -> WeatherPalette(
            c1 = MeteoColors.DeepBlue,
            c2 = MeteoColors.DarkGray,
            c3 = MeteoColors.DeepBlue.copy(alpha = 0.85f)
        )

        WeatherVisual.Stormy -> WeatherPalette(
            c1 = MeteoColors.StormBlue,
            c2 = MeteoColors.DarkGray,
            c3 = MeteoColors.StormBlue.copy(alpha = 0.85f)
        )

        WeatherVisual.Snowy -> WeatherPalette(
            c1 = MeteoColors.White,
            c2 = MeteoColors.SkyBlue.copy(alpha = 0.35f),
            c3 = MeteoColors.LightGray
        )

        WeatherVisual.Default -> WeatherPalette(
            c1 = MeteoColors.SkyBlue,
            c2 = MeteoColors.LightGray,
            c3 = MeteoColors.White
        )
    }
}

private fun radialBackgroundFor(
    palette: WeatherPalette,
    progress: Float
): Brush {
    // progress = 0..1
    val p = progress.coerceIn(0f, 1f)
    val center = Offset(0.5f, 0.15f)

    // Pour “adoucir” le changement on joue sur l’alpha global
    val a = 0.55f + 0.45f * p

    return Brush.radialGradient(
        colors = listOf(
            palette.c3.copy(alpha = 0.85f * a),
            palette.c2.copy(alpha = 0.90f * a),
            palette.c1.copy(alpha = 1.00f)
        ),
        center = center,
        radius = 1200f
    )
}

private fun haloColorFor(visual: WeatherVisual): Color {
    return when (visual) {
        WeatherVisual.Sunny -> MeteoColors.SunYellow
        WeatherVisual.Cloudy -> Color.White
        WeatherVisual.Rainy -> MeteoColors.SkyBlue
        WeatherVisual.Stormy -> MeteoColors.SunYellow
        WeatherVisual.Snowy -> MeteoColors.SkyBlue
        WeatherVisual.Default -> Color.White
    }
}