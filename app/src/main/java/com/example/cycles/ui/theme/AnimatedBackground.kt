package com.example.cycles.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit // Recibe el contenido de la pantalla
) {
    // 1. Lógica de la Animación (Igual que en WelcomeScreen)
    val infiniteTransition = rememberInfiniteTransition(label = "smooth_wave_animation")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 15000, // 15 segundos para suavidad extrema
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x_offset"
    )

    // 2. Definición del Brush Animado y Difuminado
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    // 🎯 AJUSTE CLAVE: Reducir la opacidad de los colores
    val diffusedPrimary = primaryColor.copy(alpha = 0.4f) // 40% opacidad
    val diffusedSecondary = secondaryColor.copy(alpha = 0.2f) // 20% opacidad
    val diffusedTertiary = tertiaryColor.copy(alpha = 0.1f) // 10% opacidad

    val animatedBrush = Brush.linearGradient(
        colors = listOf(diffusedPrimary, diffusedSecondary, diffusedTertiary, diffusedPrimary),
        start = Offset(xOffset * 2000f, xOffset * 2000f),
        end = Offset(xOffset * 2000f + 2000f, xOffset * 2000f + 2000f),
        tileMode = TileMode.Mirror
    )

    // 3. Contenedor del Fondo
    Column(
        modifier = modifier
            .fillMaxSize()
            // Aplica el Brush DIFUMINADO al fondo
            .background(animatedBrush)
            .padding(24.dp),
        content = content // Coloca el contenido de la pantalla aquí
    )
}