package com.example.cycles.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.cycles.data.RecommendationItem
import com.example.cycles.viewmodel.FinalRecommendationsViewModel

@Composable
fun FinalRecommendationsScreen(
    sessionId: String,
    viewModel: FinalRecommendationsViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsState()

    // carga automática de las recomendaciones finales
    LaunchedEffect(sessionId) {
        viewModel.loadFinalRecommendations(sessionId)
    }

    when (val state = uiState.value) {
        is FinalRecommendationsViewModel.UiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is FinalRecommendationsViewModel.UiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("❌ Error: ${state.message}")
            }
        }

        is FinalRecommendationsViewModel.UiState.Success -> {
            RecommendationsGrid(items = state.recommendations)
        }
    }
}

@Composable
fun RecommendationsGrid(items: List<RecommendationItem>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            RecommendationCard(item)
        }
    }
}

@Composable
fun RecommendationCard(item: RecommendationItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.7f),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(item.imageUrl),
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
