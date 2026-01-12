package com.nazam.meteo.core.ui

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * UiText = texte utilisable dans l'UI (KMP friendly).
 *
 * - Dynamic : texte direct (à éviter si possible)
 * - Resource : texte venant des strings.xml (recommandé)
 */
sealed class UiText {

    data class Dynamic(val value: String) : UiText()

    data class Resource(
        val res: StringResource,
        val args: List<Any> = emptyList()
    ) : UiText()

    /**
     * IMPORTANT :
     * Pour lire un StringResource, il faut être dans @Composable.
     */
    @Composable
    fun asString(): String {
        return when (this) {
            is Dynamic -> value
            is Resource -> stringResource(res, *args.toTypedArray())
        }
    }
}