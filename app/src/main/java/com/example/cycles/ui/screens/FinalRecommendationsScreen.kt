package com.example.cycles.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cycles.data.RecommendationItem
import com.example.cycles.ui.components.StatsPopupDialog
import com.example.cycles.viewmodel.FinalRecommendationsViewModel
import com.example.cycles.viewmodel.SessionCache
import kotlinx.coroutines.launch

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
    // El contenido principal de la pantalla
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
            RecommendationsGrid(
                items = state.recommendations,
                modifier = Modifier.weight(1f) // Ajustado para que ocupe el espacio disponible
            )

            Button(
                // 👈 3. El botón ahora llama a la nueva función del ViewModel
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecommendationsGrid(
    items: List<RecommendationItem>,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 5.dp,
            top = 40.dp,
            end = 5.dp,
            bottom = 15.dp // Espacio grande para subir el botón
        )
    ) {
        items(items) { item ->
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .fillMaxWidth()
                    .height(240.dp),
                elevation = CardDefaults.cardElevation()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    item.imageUrl?.let { url ->
                        Log.d("IMAGE_DEBUG", "Llega al cliente: $url")
                        AsyncImage(
                            model = url,
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        maxLines = 2
                    )
                }
            }
        }
    }
}
