package com.example.cycles.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionCard(
    title: String,
    icon: ImageVector,
    color1: Color,
    color2: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {


    // 🎯 Ajustar colores si está deshabilitado
    val finalColor1 = if (enabled) color1 else Color.Gray.copy(alpha = 0.5f)
    val finalColor2 = if (enabled) color2 else Color.DarkGray.copy(alpha = 0.5f)
    val finalIconTint = if (enabled) Color.White else Color.White.copy(alpha = 0.6f)
    val finalTextColor = if (enabled) Color.White else Color.White.copy(alpha = 0.6f)

    // 1. Lógica de animación del degradado
    val infiniteTransition = rememberInfiniteTransition(label = "gradient_animation_$title")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "x_offset_$title"
    )

    // Creamos el Brush animado
    val animatedBrush = Brush.linearGradient(
        colors = listOf(color1, color2, color1),
        // Esto crea un movimiento sutil de izquierda a derecha
        start = Offset(xOffset * 100f, 0f),
        end = Offset(xOffset * 100f + 500f, 500f)
    )
    // 1. Lógica de animación de ESCALA (NUEVO)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleFactor by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1.0f,
        label = "scale_animation_$title"
    )

    // 2. UI del componente
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .scale(scaleFactor)
            .height(150.dp) // Altura mínima
            .background(animatedBrush, shape = RoundedCornerShape(16.dp)) // Fondo animado
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
                enabled = enabled
            )

        ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono principal (grande y con contraste)
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color.White, // Íconos blancos para alto contraste
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.height(8.dp))

            // Título
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}