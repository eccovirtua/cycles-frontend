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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cycles.data.RecommendationItem
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


    when (state) {
        is FinalRecommendationsViewModel.UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is FinalRecommendationsViewModel.UiState.Success -> {
            val recommendations =
                (state as FinalRecommendationsViewModel.UiState.Success).recommendations

            Column(modifier = Modifier.fillMaxSize()) {



                // La cuadrícula toma el espacio restante
                RecommendationsGrid(
                    items = recommendations,
                    modifier = Modifier.weight(2f)
                )

                // El botón está ahora más arriba debido al título
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // 🧩 Limpiar y marcar reinicio
                                SessionCache.clearSession(domain)
                                SessionCache.markSessionAsReset(domain)

                                // 🧭 Volver a la pantalla de dominios
                                navController.navigate("home") {
                                    popUpTo("home") { inclusive = false }
                                }
                            } catch (e: Exception) {
                                Log.e("FinalRecScreen", "Error al reiniciar sesión", e)
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        // 🎯 Padding que separa el botón de los bordes.
                        .padding(horizontal = 25.dp, vertical = 15.dp)

                ) {
                    Text(
                        text="Reiniciar recomendaciones!",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        is FinalRecommendationsViewModel.UiState.Error -> {
            val message = (state as FinalRecommendationsViewModel.UiState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $message")
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
