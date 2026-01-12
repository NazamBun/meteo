package com.nazam.meteo.app

import androidx.compose.runtime.Composable
import com.nazam.meteo.core.ui.theme.MeteoTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Point d’entrée UI de l’application (KMP).
 * Android et iOS appellent MeteoApp().
 */
@Composable
@Preview
fun MeteoApp() {
    MeteoTheme {
        AppRoot()
    }
}