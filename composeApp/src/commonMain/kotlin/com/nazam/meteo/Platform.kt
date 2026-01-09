package com.nazam.meteo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform