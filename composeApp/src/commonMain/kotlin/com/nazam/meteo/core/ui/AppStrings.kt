package com.nazam.meteo.core.ui

/**
 * Toutes les chaines de caractères de l'app (version simple).
 * Plus tard on pourra ajouter EN, ES, etc.
 */
object AppStrings {

    enum class Key {
        AppTitle,
        AppSubtitle,

        SearchTitle,
        SearchHint,
        SearchButton,
        Searching,
        ResultsTitle,

        Loading,
        Retry,

        HourlyTitle,
        DailyTitle,

        ErrorNoInternet,
        ErrorCityNotFound,
        ErrorUnknown
    }

    fun get(key: Key): String {
        return when (key) {
            Key.AppTitle -> "Météo"
            Key.AppSubtitle -> "Choisis une ville, puis regarde la météo"

            Key.SearchTitle -> "Recherche de ville"
            Key.SearchHint -> "Ex: Paris"
            Key.SearchButton -> "Chercher"
            Key.Searching -> "Recherche..."
            Key.ResultsTitle -> "Résultats"

            Key.Loading -> "Chargement..."
            Key.Retry -> "Réessayer"

            Key.HourlyTitle -> "Heure par heure"
            Key.DailyTitle -> "Prévisions"

            Key.ErrorNoInternet -> "Pas de connexion internet"
            Key.ErrorCityNotFound -> "Ville introuvable"
            Key.ErrorUnknown -> "Erreur inconnue"
        }
    }

    fun latLon(lat: Double, lon: Double): String = "Lat $lat, Lon $lon"

    fun tempC(value: Int): String = "$value C"
    fun minTempC(value: Int): String = "Min $value C"
    fun maxTempC(value: Int): String = "Max $value C"
}