package com.nazam.meteo.feature.weather.data.remote.mapper

import com.nazam.meteo.feature.weather.data.remote.dto.GeocodingResponseDto
import com.nazam.meteo.feature.weather.domain.model.City

/**
 * Mapper : DTO -> Domain
 */
object GeocodingMapper {

    fun toDomain(dto: GeocodingResponseDto): List<City> {
        val items = dto.results ?: emptyList()

        return items.mapNotNull { item ->
            val name = item.name ?: return@mapNotNull null
            val country = item.country ?: return@mapNotNull null
            val lat = item.latitude ?: return@mapNotNull null
            val lon = item.longitude ?: return@mapNotNull null

            City(
                name = name,
                country = country,
                admin1 = item.admin1,
                latitude = lat,
                longitude = lon
            )
        }
    }
}