package com.nazam.meteo.feature.weather.data.repository

import com.nazam.meteo.core.result.AppResult
import com.nazam.meteo.feature.weather.data.remote.OpenMeteoGeocodingApi
import com.nazam.meteo.feature.weather.data.remote.mapper.GeocodingMapper
import com.nazam.meteo.feature.weather.domain.model.City
import com.nazam.meteo.feature.weather.domain.repository.CityRepository

/**
 * Impl data -> respecte le contrat du domain.
 */
class CityRepositoryImpl(
    private val api: OpenMeteoGeocodingApi
) : CityRepository {

    override suspend fun searchCity(query: String): AppResult<List<City>> {
        return when (val result = api.searchCity(query)) {
            is AppResult.Success -> AppResult.Success(GeocodingMapper.toDomain(result.data))
            is AppResult.Error -> result
        }
    }
}