package com.nazam.meteo.feature.weather.presentation.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.presentation.model.CitySearchUiState
import com.nazam.meteo.feature.weather.presentation.model.WeatherUiState
import com.nazam.meteo.feature.weather.presentation.ui.components.DailySection
import com.nazam.meteo.feature.weather.presentation.ui.components.ErrorSection
import com.nazam.meteo.feature.weather.presentation.ui.components.GlassCard
import com.nazam.meteo.feature.weather.presentation.ui.components.HeaderSection
import com.nazam.meteo.feature.weather.presentation.ui.components.HourlySection
import com.nazam.meteo.feature.weather.presentation.ui.components.LoadingSection
import com.nazam.meteo.feature.weather.presentation.ui.components.MainWeatherCard
import com.nazam.meteo.feature.weather.presentation.ui.components.SearchSection
import com.nazam.meteo.feature.weather.presentation.ui.effects.WeatherBackgroundEffects
import com.nazam.meteo.feature.weather.presentation.ui.style.WeatherVisual
import com.nazam.meteo.feature.weather.presentation.ui.style.backgroundBrushFor
import com.nazam.meteo.feature.weather.presentation.ui.style.contentColorFor
import com.nazam.meteo.feature.weather.presentation.ui.style.paletteFor
import com.nazam.meteo.feature.weather.presentation.ui.style.weatherVisualFromDescription

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

    // Animation douce quand la météo change (fond + effets)
    val changeProgress = remember { Animatable(1f) }
    LaunchedEffect(visual) {
        changeProgress.snapTo(0f)
        changeProgress.animateTo(1f, tween(durationMillis = 520))
    }

    val palette = paletteFor(visual)
    val backgroundBrush = backgroundBrushFor(palette, changeProgress.value)
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
                item { HeaderSection() }

                item {
                    GlassCard(visual = visual) {
                        SearchSection(
                            uiState = searchUiState,
                            onQueryChange = onSearchQueryChange,
                            onSearchClick = onSearchClick,
                            onCitySelected = onCitySelected
                        )
                    }
                }

                when (weatherUiState) {
                    WeatherUiState.Loading -> item {
                        GlassCard(visual = visual) { LoadingSection() }
                    }

                    is WeatherUiState.Error -> item {
                        GlassCard(visual = visual) {
                            ErrorSection(
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
                            item {
                                GlassCard(visual = visual) {
                                    HourlySection(hourly = weatherUiState.weather.hourly)
                                }
                            }
                        }

                        if (weatherUiState.weather.daily.isNotEmpty()) {
                            item {
                                GlassCard(visual = visual) {
                                    DailySection(daily = weatherUiState.weather.daily)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}