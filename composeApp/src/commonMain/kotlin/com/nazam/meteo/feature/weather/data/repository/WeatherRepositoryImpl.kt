package com.nazam.meteo.feature.weather.data.repository

import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.data.remote.OpenMeteoApi
import com.nazam.meteo.feature.weather.data.remote.mapper.WeatherMapper
import com.nazam.meteo.feature.weather.domain.model.Weather
import com.nazam.meteo.feature.weather.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: OpenMeteoApi
) : WeatherRepository {

    override suspend fun getWeather(latitude: Double, longitude: Double): AppResult<Weather> {
        return when (val result = api.fetchWeather(latitude, longitude)) {
            is AppResult.Success -> {
                AppResult.Success(
                    WeatherMapper.toDomain(
                        dto = result.data,
                        city = "Ma position"
                    )
                )
            }

            is AppResult.Error -> result
        }
    }
}