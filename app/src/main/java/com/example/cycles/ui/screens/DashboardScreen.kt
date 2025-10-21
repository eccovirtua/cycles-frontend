package com.example.cycles.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cycles.data.UserDashboardStats
import com.example.cycles.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
            uiState.latestStats != null -> {
                DashboardContent(
                    latestStats = uiState.latestStats!!,
                    statsForAnimationStart = uiState.statsBefore ?: uiState.latestStats!!,
                    animate = uiState.triggerAnimation,
                    onAnimationShown = viewModel::animationShown
                )
            }
            else -> {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text("No dashboard data available.")
                }
            }
        }
    }
}

@Composable
fun DashboardContent(
    latestStats: UserDashboardStats,
    statsForAnimationStart: UserDashboardStats,
    animate: Boolean,
    onAnimationShown: () -> Unit
) {
    LaunchedEffect(animate) {
        if (animate) {
            kotlinx.coroutines.delay(1000L)
            onAnimationShown()
        }
    }

    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Card de Estadísticas Globales ---
        item {
            StatsCard(title = "Estadísticas Globales") {
                AnimatedStatRow(label = "Sesiones Totales", startValue = statsForAnimationStart.totalSessions, endValue = latestStats.totalSessions, trigger = animate)
                AnimatedStatRow(label = "Sesiones Finalizadas", startValue = statsForAnimationStart.finishedSessions, endValue = latestStats.finishedSessions, trigger = animate)
                AnimatedStatRow(label = "Interacciones Totales", startValue = statsForAnimationStart.totalItemsInteracted, endValue = latestStats.totalItemsInteracted, trigger = animate)
                AnimatedStatRow(label = "Items Aceptados", startValue = statsForAnimationStart.totalItemsLiked, endValue = latestStats.totalItemsLiked, trigger = animate)
                AnimatedStatRow(label = "Items Rechazados", startValue = statsForAnimationStart.totalItemsRejected, endValue = latestStats.totalItemsRejected, trigger = animate)
                AnimatedStatRow(label = "Recomendaciones Finales", startValue = statsForAnimationStart.totalFinalRecsGenerated, endValue = latestStats.totalFinalRecsGenerated, trigger = animate)
            }
        }

        // --- Card de Tiempo Total e Índice de Calidad ---
        item {
            StatsCard(title = "Métricas Clave") {
                AnimatedStatRowFloat(label = "Horas Interaccionando", startValue = statsForAnimationStart.totalTimeStats.hoursInteracting, endValue = latestStats.totalTimeStats.hoursInteracting, trigger = animate)
                AnimatedStatRowFloat(label = "Horas de Contenido", startValue = statsForAnimationStart.totalTimeStats.hoursFromFinalRecs, endValue = latestStats.totalTimeStats.hoursFromFinalRecs, trigger = animate)
                AnimatedStatRowFloat(label = "Índice de Calidad Global", startValue = statsForAnimationStart.totalAvgQualityScore, endValue = latestStats.totalAvgQualityScore, trigger = animate)
            }
        }

        // --- Cards por Dominio ---
        latestStats.domainStats.toSortedMap().forEach { (domain, domainStatsAfter) ->
            val domainStatsBefore = statsForAnimationStart.domainStats[domain] ?: domainStatsAfter

            item {
                StatsCard(title = "Dominio: ${domain.replaceFirstChar { it.uppercase() }}") {
                    AnimatedStatRow(label = "Sesiones Totales", startValue = domainStatsBefore.totalSessions, endValue = domainStatsAfter.totalSessions, trigger = animate)
                    AnimatedStatRow(label = "Sesiones Finalizadas", startValue = domainStatsBefore.finishedSessions, endValue = domainStatsAfter.finishedSessions, trigger = animate)
                    AnimatedStatRow(label = "Items Aceptados", startValue = domainStatsBefore.itemsLiked, endValue = domainStatsAfter.itemsLiked, trigger = animate)
                    AnimatedStatRow(label = "Items Rechazados", startValue = domainStatsBefore.itemsRejected, endValue = domainStatsAfter.itemsRejected, trigger = animate)
                    AnimatedStatRowFloat(label = "Índice de Calidad", startValue = domainStatsBefore.avgQualityScore, endValue = domainStatsAfter.avgQualityScore, trigger = animate)
                    AnimatedStatRowFloat(label = "Horas Interaccionando", startValue = domainStatsBefore.timeStats.hoursInteracting, endValue = domainStatsAfter.timeStats.hoursInteracting, trigger = animate)
                    AnimatedStatRowFloat(label = "Horas de Contenido", startValue = domainStatsBefore.timeStats.hoursFromFinalRecs, endValue = domainStatsAfter.timeStats.hoursFromFinalRecs, trigger = animate)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
fun StatsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            HorizontalDivider()
            content()
        }
    }
}

// --------------------------------------------------------------------------
// ✅ FUNCIONES ANIMADAS CORREGIDAS PARA EL DASHBOARD
// --------------------------------------------------------------------------

@Composable
fun AnimatedStatRow(label: String, startValue: Int, endValue: Int, trigger: Boolean) {
    var startAnimation by remember { mutableStateOf(false) }

    val animatedValue by animateIntAsState(
        targetValue = if (startAnimation) endValue else startValue,
        animationSpec = tween(durationMillis = 800, delayMillis = 200),
        label = "stat_animation_int_dash"
    )

    val animatedFontSize by animateFloatAsState(
        targetValue = if (startAnimation) 18f else 16f,
        animationSpec = tween(
            durationMillis = 800, // ✅ CORRECCIÓN: Usar la misma duración para que vuelva a 16f
            delayMillis = 200
        ),
        label = "stat_font_size_int"
    )

    LaunchedEffect(trigger) {
        if (trigger) {
            startAnimation = true
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
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            maxLines = 1
        )
        Box(
            modifier = Modifier.wrapContentWidth(Alignment.End)
        ) {
            Row(horizontalArrangement = Arrangement.End) {
                Text(
                    text = animatedValue.toString(),
                    fontSize = animatedFontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = if (animatedValue > startValue && startAnimation)
                        MaterialTheme.colorScheme.primary else
                        MaterialTheme.colorScheme.onSurface
                )
                val difference = endValue - startValue
                if (startAnimation && difference > 0) {
                    Text(
                        text = " (+${difference})",
                        fontSize = 16.sp, // No animamos el tamaño del '+X'
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun AnimatedStatRowFloat(label: String, startValue: Float, endValue: Float, trigger: Boolean) {
    var startAnimation by remember { mutableStateOf(false) }

    val animatedValue by animateFloatAsState(
        targetValue = if (startAnimation) endValue else startValue,
        animationSpec = tween(durationMillis = 800, delayMillis = 200),
        label = "stat_animation_float_dash"
    )

    val animatedFontSize by animateFloatAsState(
        targetValue = if (startAnimation) 18f else 16f,
        animationSpec = tween(
            durationMillis = 800, // ✅ CORRECCIÓN: Usar la misma duración para que vuelva a 16f
            delayMillis = 200
        ),
        label = "stat_font_size_float_dash"
    )

    LaunchedEffect(trigger) {
        if (trigger) {
            startAnimation = true
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
            modifier = Modifier.weight(1f).padding(end = 8.dp),
            maxLines = 1
        )
        Box(
            modifier = Modifier.wrapContentWidth(Alignment.End)
        ) {
            Row(horizontalArrangement = Arrangement.End) {
                Text(
                    text = "%.2f".format(animatedValue),
                    fontSize = animatedFontSize.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = if (animatedValue > startValue + 0.01f && startAnimation)
                        MaterialTheme.colorScheme.primary else
                        MaterialTheme.colorScheme.onSurface
                )
                val difference = endValue - startValue
                if (startAnimation && difference > 0.01f) {
                    Text(
                        text = " (+%.2f)".format(difference),
                        fontSize = 16.sp, // No animamos el tamaño del '+X'
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}