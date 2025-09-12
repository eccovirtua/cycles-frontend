package com.example.cycles.ui.screens


import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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

import coil.compose.AsyncImage
import com.example.cycles.data.RecommendationItem
import com.example.cycles.viewmodel.FinalRecommendationsViewModel
import com.example.cycles.viewmodel.SessionCache
import kotlinx.coroutines.launch

@Composable
fun FinalRecommendationsScreen(
    domain: String,
    sessionId: String,
    viewModel: FinalRecommendationsViewModel = hiltViewModel(),
    onRestart: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // 👉 Solo una vez al entrar en la pantalla
    LaunchedEffect(sessionId) {
        viewModel.loadFinalRecommendations(sessionId)
    }

    when (state) {
        is FinalRecommendationsViewModel.UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is FinalRecommendationsViewModel.UiState.Success -> {
            val recommendations = (state as FinalRecommendationsViewModel.UiState.Success).recommendations
            val coroutineScope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                // 🔥 El grid ocupa todo el espacio disponible
                RecommendationsGrid(
                    items = recommendations,
                    modifier = Modifier.weight(1f)
                )

                Spacer(Modifier.height(12.dp))

                // 🔥 El botón queda siempre visible abajo
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.restartSession(domain)
                            SessionCache.clearSession(domain)
                            onRestart(domain)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Reiniciar sesión")
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
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items.size) { index ->
            val item = items[index]

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(240.dp),
                elevation = CardDefaults.cardElevation()
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    // Imagen (si hay URL)
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

                    Spacer(Modifier.height(8.dp))

                    // Título
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        maxLines = 2
                    )
                }
            }
        }
    }
}