package com.nazam.meteo

import androidx.compose.runtime.Composable
import com.nazam.meteo.app.MeteoApp

/**
 * Bridge (pont) temporaire.
 * L’ancien template appelait App().
 * Maintenant, notre vraie app s’appelle MeteoApp().
 *
 * Plus tard, quand tout sera migré, on pourra supprimer ce fichier.
 */
@Composable
fun App() {
    MeteoApp()
}