package com.nazam.meteo.core.network

import io.ktor.client.HttpClient

/**
 * expect = déclaration commune
 * actual = version Android / iOS
 */
expect fun createHttpClient(): HttpClient