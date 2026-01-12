package com.nazam.meteo.feature.weather.presentation.ui.effects

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import com.nazam.meteo.core.ui.theme.MeteoColors
import com.nazam.meteo.feature.weather.presentation.ui.style.WeatherVisual
import com.nazam.meteo.feature.weather.presentation.ui.style.isDarkBackground

@Composable
fun WeatherBackgroundEffects(
    visual: WeatherVisual,
    progress: Float
) {
    val baseAlpha = (0.35f + 0.65f * progress).coerceIn(0f, 1f)

    val infinite = rememberInfiniteTransition(label = "BgInfinite")

    val cloudShift by infinite.animateFloat(
        initialValue = -0.06f,
        targetValue = 0.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CloudShift"
    )

    val twinkle by infinite.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.00f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Twinkle"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Clouds
        if (visual == WeatherVisual.Cloudy || visual == WeatherVisual.Rainy || visual == WeatherVisual.Stormy) {
            val baseCloudAlpha = if (isDarkBackground(visual)) 0.16f * baseAlpha else 0.22f * baseAlpha
            val dx = w * cloudShift

            drawRealisticCloud(
                topLeft = Offset(w * 0.16f + dx, h * 0.16f),
                size = Size(w * 0.92f, h * 0.18f),
                alpha = baseCloudAlpha,
                isDark = isDarkBackground(visual)
            )
            drawRealisticCloud(
                topLeft = Offset(w * 0.02f - dx, h * 0.36f),
                size = Size(w * 0.98f, h * 0.20f),
                alpha = baseCloudAlpha * 0.90f,
                isDark = isDarkBackground(visual)
            )
            drawRealisticCloud(
                topLeft = Offset(w * 0.22f + dx * 0.7f, h * 0.60f),
                size = Size(w * 0.82f, h * 0.16f),
                alpha = baseCloudAlpha * 0.78f,
                isDark = isDarkBackground(visual)
            )
        }

        // Stars (storm)
        if (visual == WeatherVisual.Stormy) {
            val starBase = MeteoColors.SunYellow.copy(alpha = 0.40f * baseAlpha)

            val stars = listOf(
                Offset(w * 0.15f, h * 0.12f),
                Offset(w * 0.32f, h * 0.20f),
                Offset(w * 0.55f, h * 0.14f),
                Offset(w * 0.74f, h * 0.24f),
                Offset(w * 0.86f, h * 0.10f),
                Offset(w * 0.64f, h * 0.32f)
            )

            stars.forEachIndexed { index, p ->
                val pulse = (0.55f + 0.45f * twinkle) * (0.88f + index * 0.02f)
                val r = 2.6f + index * 0.35f

                drawCircle(
                    color = starBase.copy(alpha = starBase.alpha * pulse),
                    radius = r,
                    center = p
                )

                drawCircle(
                    color = starBase.copy(alpha = starBase.alpha * 0.30f * pulse),
                    radius = r * 2.6f,
                    center = p
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRealisticCloud(
    topLeft: Offset,
    size: Size,
    alpha: Float,
    isDark: Boolean
) {
    val x = topLeft.x
    val y = topLeft.y
    val w = size.width
    val h = size.height

    val shadow = if (isDark) Color.Black.copy(alpha = 0.10f * alpha) else Color.Black.copy(alpha = 0.08f * alpha)

    val highlightTop = Color.White.copy(alpha = (alpha * if (isDark) 0.75f else 0.90f).coerceIn(0f, 1f))
    val highlightBottom = Color.White.copy(alpha = (alpha * if (isDark) 0.25f else 0.35f).coerceIn(0f, 1f))

    val mainBrush = Brush.linearGradient(
        colors = listOf(highlightTop, highlightBottom),
        start = Offset(x, y),
        end = Offset(x, y + h)
    )

    val shadowBrush = Brush.linearGradient(
        colors = listOf(shadow.copy(alpha = shadow.alpha * 0.0f), shadow),
        start = Offset(x, y + h * 0.35f),
        end = Offset(x, y + h)
    )

    val baseRectTop = y + h * 0.42f
    val rectHeight = h * 0.52f

    drawRoundRect(
        brush = shadowBrush,
        topLeft = Offset(x, baseRectTop + h * 0.06f),
        size = Size(w, rectHeight),
        cornerRadius = CornerRadius(h, h),
        style = Fill
    )

    drawRoundRect(
        brush = mainBrush,
        topLeft = Offset(x, baseRectTop),
        size = Size(w, rectHeight),
        cornerRadius = CornerRadius(h, h),
        style = Fill
    )

    fun bubble(cx: Float, cy: Float, r: Float) {
        drawCircle(color = shadow, radius = r * 1.05f, center = Offset(cx + r * 0.06f, cy + r * 0.10f))
        drawCircle(brush = mainBrush, radius = r, center = Offset(cx, cy))
    }

    val cy = y + h * 0.52f
    bubble(x + w * 0.22f, cy, h * 0.30f)
    bubble(x + w * 0.40f, y + h * 0.42f, h * 0.38f)
    bubble(x + w * 0.58f, y + h * 0.50f, h * 0.33f)
    bubble(x + w * 0.74f, y + h * 0.55f, h * 0.26f)

    val highlightLine = Color.White.copy(alpha = (alpha * 0.20f).coerceIn(0f, 1f))
    drawRoundRect(
        color = highlightLine,
        topLeft = Offset(x + w * 0.10f, y + h * 0.40f),
        size = Size(w * 0.80f, h * 0.06f),
        cornerRadius = CornerRadius(h, h),
        style = Fill
    )
}