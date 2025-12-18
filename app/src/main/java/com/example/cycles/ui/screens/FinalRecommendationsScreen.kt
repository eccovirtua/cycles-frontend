package com.example.cycles.ui.screens

import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cycles.data.ItemType
import com.example.cycles.data.RecommendationItem
import com.example.cycles.navigation.Screen
import com.example.cycles.ui.components.StatsPopupDialog
import com.example.cycles.viewmodel.FinalRecommendationsViewModel
import com.example.cycles.viewmodel.SessionCache
import kotlinx.coroutines.launch

// --- Pantalla Principal (sin cambios grandes) ---
@Composable
fun FinalRecommendationsScreen(
    domain: String,
    sessionId: String,
    navController: NavController,
    viewModel: FinalRecommendationsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(sessionId) {
        if (sessionId.isNotBlank()) {
            viewModel.loadFinalRecommendations(sessionId)
        }
    }


    if (state.shouldShowStatsPopup && state.statsBeforeSession != null && state.statsAfterSession != null) {
        StatsPopupDialog(
            statsBefore = state.statsBeforeSession!!,
            statsAfter = state.statsAfterSession!!,
            onDismiss = {
                viewModel.dismissStatsPopup()
                coroutineScope.launch {
                    SessionCache.clearSession(domain)
                    SessionCache.markSessionAsReset(domain)
                    navController.navigate("dashboard?animate=true") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }
        )
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error: ${state.error}")
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            RecommendationsGrid( // Llama a la grid actualizada
                items = state.recommendations,
                modifier = Modifier.weight(1f),
                navController = navController,
                domain = domain
            )

            Button(
                onClick = { viewModel.onRestartClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp, vertical = 15.dp)

            ) {
                Text(
                    text = "Ver resumen!",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// --- Grid de Recomendaciones (Actualizada) ---
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendationsGrid(
    items: List<RecommendationItem>,
    navController: NavController,
    modifier: Modifier = Modifier,
    domain: String
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 12.dp, // Aumentar un poco el padding lateral
            top = 40.dp,
            end = 12.dp,   // Aumentar un poco el padding lateral
            bottom = 15.dp
        ),
        // Espaciado entre las tarjetas (horizontal y vertical)
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items, key = { it.itemId }) { item -> // Añadir key por buena práctica
            // 🎯 USA EL NUEVO COMPOSABLE ANIMADO
            AnimatedRecommendationCard(item = item, onClick = {
                val itemType = try {
                    ItemType.valueOf(domain.uppercase())
                } catch (e: Exception) { // Catch the exception
                    Log.e("FinalRecsClick", "Dominio inválido recibido: $domain", e)
                    ItemType.BOOK // <-- FIX: Assign a default fallback value
                } // <-- The variable 'itemType' now ALWAYS has a value

                Log.d("FinalRecsClick", "Item clicked: ${item.itemId}. Type: $itemType")
                navController.navigate(Screen.ItemDetail.createRoute(item.itemId, itemType))
            })
        }
    }
}


@Composable
fun AnimatedRecommendationCard(item: RecommendationItem, onClick: () -> Unit) {

    // 1. Lógica de animación de "flote" (igual que en HomeScreen)
    val infiniteTransition = rememberInfiniteTransition(label = "floating_card_${item.itemId}")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -4f, // Sube
        targetValue = 4f,  // Baja
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y_card_${item.itemId}"
    )

    // 2. La Card, con el modifier graphicsLayer
    Card(
        modifier = Modifier
            .fillMaxWidth() // Ocupa el ancho disponible en la celda del grid
            .height(240.dp) // Mantenemos la altura fija
            .graphicsLayer { translationY = offsetY }
            .clickable(onClick = onClick), // <-- APLICA LA ANIMACIÓN AQUÍ
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Sombra sutil
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top // Alinea contenido arriba
        ) {
            Spacer(Modifier.height(8.dp))

            // Imagen (ocupa más espacio)
            item.imageUrl?.let { url ->
                Log.d("IMAGE_DEBUG", "Llega al cliente: $url")
                AsyncImage(
                    model = url,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .weight(1f)
                )
            } ?: Box( // Placeholder si no hay imagen
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            // Espacio entre imagen y texto
            Spacer(Modifier.height(8.dp))

            // Texto (con padding y limitado a 2 líneas)
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 8.dp), // Padding inferior
                maxLines = 2, // Limita a 2 líneas
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis // Añade "..." si es largo
            )
        }
    }
}