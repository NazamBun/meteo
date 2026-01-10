package com.nazam.meteo.feature.weather.domain.model

/**
 * City = modèle métier (domain).
 * Simple, sans JSON, sans réseau.
 */
data class City(
    val name: String,
    val country: String,
    val admin1: String?, // région (ex: Île-de-France) - peut être null
    val latitude: Double,
    val longitude: Double
) {
    /**
     * Petit texte propre à afficher dans l'UI.
     */
    fun displayName(): String {
        return if (admin1.isNullOrBlank()) {
            "$name, $country"
        } else {
            "$name, $admin1, $country"
        }
    }
}