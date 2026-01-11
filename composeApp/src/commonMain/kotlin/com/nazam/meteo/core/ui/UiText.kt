package com.nazam.meteo.core.ui

/**
 * UiText = texte utilisable dans l'UI.
 * - Dynamic : texte direct (rare)
 * - StringKey : texte venant de AppStrings (recommandé)
 */
sealed class UiText {

    data class Dynamic(val value: String) : UiText()
    data class StringKey(val key: AppStrings.Key) : UiText()

    fun asString(): String {
        return when (this) {
            is Dynamic -> value
            is StringKey -> AppStrings.get(key)
        }
    }
}