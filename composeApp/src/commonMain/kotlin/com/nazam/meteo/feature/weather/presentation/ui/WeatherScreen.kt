package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
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

    val changeProgress = remember { Animatable(1f) }
    LaunchedEffect(visual) {
        changeProgress.snapTo(0f)
        changeProgress.animateTo(1f, tween(durationMillis = 520))
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
                    WeatherUiState.Loading -> item {
                        GlassCard(visual = visual) { LoadingContent() }
                    }

                    is WeatherUiState.Error -> item {
                        GlassCard(visual = visual) {
                            ErrorContent(
                                message = weatherUiState.message.asString(),
                                onRetry = onRetry
                            )
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
        Text(text = stringResource(Res.string.search_title), style = MaterialTheme.typography.titleMedium)
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
                text = if (uiState.isLoading) stringResource(Res.string.searching)
                else stringResource(Res.string.search_button)
            )
        }
    }

    uiState.errorMessage?.let { msg ->
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = msg.asString(), style = MaterialTheme.typography.bodyMedium)
    }

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

@Composable
private fun MainWeatherCard(
    weather: Weather,
    visual: WeatherVisual
) {
    val icon = mainWeatherIconFor(visual, weather.weatherCode)

    val haloColor = haloColorFor(visual)
    val haloAlpha by animateFloatAsState(
        targetValue = if (isDarkBackground(visual)) 1.00f else 0.85f,
        animationSpec = tween(450),
        label = "HaloAlpha"
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
            Box(
                modifier = Modifier.size(100.dp),
                contentAlignment = Alignment.Center
            ) {
                GlowHaloStrong(color = haloColor, alpha = haloAlpha)

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
                        modifier = Modifier.size(82.dp)
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

@Composable
private fun GlowHaloStrong(
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
                        color.copy(alpha = 0.12f * alpha),
                        color.copy(alpha = 0.26f * alpha),
                        color.copy(alpha = 0.00f)
                    ),
                    radius = 320f
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
                        color.copy(alpha = 0.22f * alpha),
                        color.copy(alpha = 0.00f)
                    ),
                    radius = 200f
                )
            )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        color.copy(alpha = 0.55f * alpha),
                        color.copy(alpha = 0.00f)
                    ),
                    radius = 95f
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

/* Background effects */

@Composable
private fun WeatherBackgroundEffects(
    visual: WeatherVisual,
    progress: Float
) {
    val baseAlpha = (0.35f + 0.65f * progress).coerceIn(0f, 1f)

    val infinite = rememberInfiniteTransition(label = "BgInfinite")

    val cloudShift by infinite.animateFloat(
        initialValue = -0.06f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CloudShift"
    )

    val twinkle by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.00f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Twinkle"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        if (visual == WeatherVisual.Cloudy || visual == WeatherVisual.Rainy || visual == WeatherVisual.Stormy) {
            val baseCloudAlpha = if (isDarkBackground(visual)) {
                0.16f * baseAlpha
            } else {
                0.22f * baseAlpha
            }

            val dx = w * cloudShift

            drawRealisticCloud(
                topLeft = Offset(w * 0.16f + dx, h * 0.16f),
                size = Size(w * 0.92f, h * 0.18f),
                alpha = baseCloudAlpha,
                isDark = isDarkBackground(visual)
            )
            drawRealisticCloud(
                topLeft = Offset(w * 0.02f - dx, h * 0.36f),
                size = Size(w * 0.98f, h * 0.20f),
                alpha = baseCloudAlpha * 0.90f,
                isDark = isDarkBackground(visual)
            )
            drawRealisticCloud(
                topLeft = Offset(w * 0.22f + dx * 0.7f, h * 0.60f),
                size = Size(w * 0.82f, h * 0.16f),
                alpha = baseCloudAlpha * 0.78f,
                isDark = isDarkBackground(visual)
            )
        }

        if (visual == WeatherVisual.Stormy) {
            val starBase = MeteoColors.SunYellow.copy(alpha = 0.40f * baseAlpha)

            val stars = listOf(
                Offset(w * 0.15f, h * 0.12f),
                Offset(w * 0.32f, h * 0.20f),
                Offset(w * 0.55f, h * 0.14f),
                Offset(w * 0.74f, h * 0.24f),
                Offset(w * 0.86f, h * 0.10f),
                Offset(w * 0.64f, h * 0.32f)
            )

            stars.forEachIndexed { index, p ->
                val pulse = (0.55f + 0.45f * twinkle) * (0.88f + index * 0.02f)
                val r = 2.6f + index * 0.35f

                drawCircle(
                    color = starBase.copy(alpha = starBase.alpha * pulse),
                    radius = r,
                    center = p
                )

                drawCircle(
                    color = starBase.copy(alpha = starBase.alpha * 0.30f * pulse),
                    radius = r * 2.6f,
                    center = p
                )
            }
        }
    }
}

/**
 * Nuage plus réaliste (toujours simple) :
 * - 2 couches (ombre + lumière)
 * - dégradé vertical dans la couche de lumière
 * - petit highlight en haut
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRealisticCloud(
    topLeft: Offset,
    size: Size,
    alpha: Float,
    isDark: Boolean
) {
    val x = topLeft.x
    val y = topLeft.y
    val w = size.width
    val h = size.height

    val base = if (isDark) Color.White.copy(alpha = alpha) else Color.White.copy(alpha = alpha)
    val shadow = if (isDark) Color.Black.copy(alpha = 0.10f * alpha) else Color.Black.copy(alpha = 0.08f * alpha)

    val highlightTop = Color.White.copy(alpha = (alpha * if (isDark) 0.75f else 0.90f).coerceIn(0f, 1f))
    val highlightBottom = Color.White.copy(alpha = (alpha * if (isDark) 0.25f else 0.35f).coerceIn(0f, 1f))

    val mainBrush = Brush.linearGradient(
        colors = listOf(highlightTop, highlightBottom),
        start = Offset(x, y),
        end = Offset(x, y + h)
    )

    val shadowBrush = Brush.linearGradient(
        colors = listOf(shadow.copy(alpha = shadow.alpha * 0.0f), shadow),
        start = Offset(x, y + h * 0.35f),
        end = Offset(x, y + h)
    )

    val baseRectTop = y + h * 0.42f
    val rectHeight = h * 0.52f

    // Ombre douce (fond)
    drawRoundRect(
        brush = shadowBrush,
        topLeft = Offset(x, baseRectTop + h * 0.06f),
        size = Size(w, rectHeight),
        cornerRadius = CornerRadius(h, h),
        style = Fill
    )

    // Base principale avec dégradé
    drawRoundRect(
        brush = mainBrush,
        topLeft = Offset(x, baseRectTop),
        size = Size(w, rectHeight),
        cornerRadius = CornerRadius(h, h),
        style = Fill
    )

    // Bulles du nuage (2 couches : ombre + lumière)
    fun bubble(cx: Float, cy: Float, r: Float) {
        drawCircle(color = shadow, radius = r * 1.05f, center = Offset(cx + r * 0.06f, cy + r * 0.10f))
        drawCircle(brush = mainBrush, radius = r, center = Offset(cx, cy))
    }

    val cy = y + h * 0.52f
    bubble(x + w * 0.22f, cy, h * 0.30f)
    bubble(x + w * 0.40f, y + h * 0.42f, h * 0.38f)
    bubble(x + w * 0.58f, y + h * 0.50f, h * 0.33f)
    bubble(x + w * 0.74f, y + h * 0.55f, h * 0.26f)

    // Petit highlight en haut (fine ligne douce)
    val highlightLine = Color.White.copy(alpha = (alpha * 0.20f).coerceIn(0f, 1f))
    drawRoundRect(
        color = highlightLine,
        topLeft = Offset(x + w * 0.10f, y + h * 0.40f),
        size = Size(w * 0.80f, h * 0.06f),
        cornerRadius = CornerRadius(h, h),
        style = Fill
    )
}

/* Weather icons */

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

private fun isSunnyCode(code: Int): Boolean = code == 0
private fun isCloudyCode(code: Int): Boolean = code in listOf(1, 2, 3, 45, 48)
private fun isRainCode(code: Int): Boolean = code in 51..67 || code in 80..82
private fun isSnowCode(code: Int): Boolean = code in 71..77 || code in 85..86
private fun isThunderstormCode(code: Int): Boolean = code in 95..99

/* Visual + colors */

private enum class WeatherVisual {
    Sunny, Cloudy, Rainy, Stormy, Snowy, Default
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
    return visual == WeatherVisual.Rainy || visual == WeatherVisual.Stormy
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
        WeatherVisual.Sunny -> WeatherPalette(MeteoColors.SkyBlue, MeteoColors.SkyBlue, MeteoColors.SunYellow)
        WeatherVisual.Cloudy -> WeatherPalette(MeteoColors.LightGray, MeteoColors.SkyBlue.copy(alpha = 0.65f), MeteoColors.White)
        WeatherVisual.Rainy -> WeatherPalette(MeteoColors.DeepBlue, MeteoColors.DarkGray, MeteoColors.DeepBlue)
        WeatherVisual.Stormy -> WeatherPalette(MeteoColors.StormBlue, MeteoColors.DarkGray, MeteoColors.DeepBlue)
        WeatherVisual.Snowy -> WeatherPalette(MeteoColors.White, MeteoColors.SkyBlue.copy(alpha = 0.35f), MeteoColors.LightGray)
        WeatherVisual.Default -> WeatherPalette(MeteoColors.SkyBlue, MeteoColors.LightGray, MeteoColors.White)
    }
}

private fun radialBackgroundFor(palette: WeatherPalette, progress: Float): Brush {
    val p = progress.coerceIn(0f, 1f)
    val radius = 1400f + (1f - p) * 200f

    return Brush.radialGradient(
        colors = listOf(
            palette.c3.copy(alpha = 0.98f),
            palette.c2.copy(alpha = 0.95f),
            palette.c1.copy(alpha = 1.00f)
        ),
        center = Offset.Unspecified,
        radius = radius
    )
}

private fun haloColorFor(visual: WeatherVisual): Color {
    return when (visual) {
        WeatherVisual.Sunny -> MeteoColors.SunYellow
        WeatherVisual.Cloudy -> MeteoColors.SkyBlue
        WeatherVisual.Rainy -> MeteoColors.SkyBlue
        WeatherVisual.Stormy -> MeteoColors.SunYellow
        WeatherVisual.Snowy -> MeteoColors.SkyBlue
        WeatherVisual.Default -> MeteoColors.SkyBlue
    }
}