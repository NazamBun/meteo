package com.nazam.meteo.core.result

/**
 * Toutes les erreurs possibles de l’app.
 * On garde ça simple et lisible.
 */
sealed class AppError {
    data object Network : AppError()
    data object NotFound : AppError()
    data class Unknown(val message: String? = null) : AppError()
}