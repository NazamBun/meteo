package com.nazam.meteo.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Point d’entrée UI de l’application (KMP).
 * Android et iOS appellent MeteoApp().
 */
@Composable
@Preview
fun MeteoApp() {
    MaterialTheme {
        AppRoot()
    }
}