package com.nazam.meteo.core.result

/**
 * Result = soit Success avec une donnée,
 * soit Error avec une erreur.
 */
sealed class AppResult<out T> {

    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val error: AppError) : AppResult<Nothing>()

    inline fun <R> map(transform: (T) -> R): AppResult<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Error -> this
        }
    }
}