package com.example.cycles.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cycles.data.UserDashboardStats

@Composable
fun StatsPopupDialog(
    statsBefore: UserDashboardStats?,
    statsAfter: UserDashboardStats,
    onDismiss: () -> Unit
) {
    // 1. Trigger de animación para el diálogo
    var triggerAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Se activa inmediatamente al componerse el diálogo
        triggerAnimation = true
    }

    val safeStatsBefore = statsBefore ?: statsAfter
    val totalHoursAddedBefore = safeStatsBefore.totalTimeStats.hoursFromFinalRecs
    val totalHoursAddedAfter = statsAfter.totalTimeStats.hoursFromFinalRecs
    val hoursDifference = totalHoursAddedAfter - totalHoursAddedBefore

    val qualityBefore = safeStatsBefore.totalAvgQualityScore
    val qualityAfter = statsAfter.totalAvgQualityScore


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Sesión Completada!", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Tus métricas clave han aumentado:", style = MaterialTheme.typography.bodyMedium)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 4.dp),
                    thickness = DividerDefaults.Thickness,
                    color = DividerDefaults.color
                )

                // --- METRICA 1: HORAS DE CONTENIDO AGREGADAS ---
                AnimatedStatRowFloat(
                    label = "Horas de Contenido Agregadas",
                    startValue = 0.0f,
                    endValue = hoursDifference,
                    trigger = triggerAnimation
                )

                // --- METRICA 2: ÍNDICE DE CALIDAD GLOBAL ---
                AnimatedStatRowFloat(
                    label = "Índice de Calidad Global",
                    startValue = qualityBefore,
                    endValue = qualityAfter,
                    trigger = triggerAnimation
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Confirmar")
            }
        }
    )
}

// ----------------------------------------------------
// ✅ FUNCIÓN ANIMADA CORREGIDA Y CONSISTENTE
//    (Asegúrate de que ESTA sea la ÚNICA definición de AnimatedStatRowFloat que uses)
// ----------------------------------------------------

@Composable
fun AnimatedStatRowFloat(label: String, startValue: Float, endValue: Float, trigger: Boolean) {
    // Usamos el patrón de estado interno para consistencia con el Dashboard
    var startAnimation by remember { mutableStateOf(false) }

    // 1. Animación del VALOR:
    val animatedValue by animateFloatAsState(
        targetValue = if (startAnimation) endValue else startValue, // 👈 Usamos startAnimation
        animationSpec = tween(durationMillis = 800, delayMillis = 200),
        label = "stat_animation_float"
    )

    // 2. Animación del TAMAÑO (Inflación/Desinflación):
    val animatedFontSize by animateFloatAsState(
        targetValue = if (startAnimation) 18f else 16f, // 👈 Usamos startAnimation
        animationSpec = tween(
            durationMillis = 800, // ✅ CORRECCIÓN: Misma duración para que regrese a 16f
            delayMillis = 200
        ),
        label = "stat_font_size_float"
    )

    // 3. Activación del Estado Interno:
    LaunchedEffect(trigger) {
        if (trigger) {
            startAnimation = true // Esto asegura que la animación se inicie UNA VEZ
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f).padding(end = 8.dp)
        )

        Box(
            modifier = Modifier.wrapContentWidth(Alignment.End)
        ) {
            Row(horizontalArrangement = Arrangement.End) {
                // El color se basa en si el valor animado ha superado el inicio y si la animación está activa
                val color = if (animatedValue > startValue + 0.01f && startAnimation)
                    MaterialTheme.colorScheme.primary else
                    MaterialTheme.colorScheme.onSurface

                Text(
                    text = "%.2f".format(animatedValue),
                    fontSize = animatedFontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = color
                )

                val difference = endValue - startValue
                if (startAnimation && difference > 0.01f) {
                    // El texto de ganancia usa el tamaño animado para sincronía
                    Text(
                        text = " (+%.2f)".format(difference),
                        fontSize = animatedFontSize.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}