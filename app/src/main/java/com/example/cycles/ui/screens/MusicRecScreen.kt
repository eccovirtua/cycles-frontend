package com.example.cycles.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cycles.data.RecommendationItem
import com.example.cycles.viewmodel.MusicRecViewModel

@Composable
fun MusicRecScreen(
    viewModel: MusicRecViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    val initialItems = listOf(
        RecommendationItem(
            itemId = "lf-Nirvana_Smells Like Teen Spirit",
            title = "Smells Like Teen Spirit",
            distance = 0.0,
            imageUrl = "https://lastfm.freetls.fastly.net/i/u/300x300/2a96cbd8b46e442fc41c2b86b821562f.png"
        ),
        RecommendationItem(
            itemId = "lf-A\$AP Rocky_Fashion Killa",
            title = "Fashion Killa",
            distance = 0.0,
            imageUrl = "https://lastfm.freetls.fastly.net/i/u/300x300/2a96cbd8b46e442fc41c2b86b821562f.png"
        ),
        RecommendationItem(
            itemId = "lf-Tyler, The Creator_SUGAR ON MY TONGUE",
            title = "SUGAR ON MY TONGUE",
            distance = 0.0,
            imageUrl = "https://lastfm.freetls.fastly.net/i/u/300x300/2a96cbd8b46e442fc41c2b86b821562f.png"
        )
    )

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("¿Qué podrías escuchar hoy?", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        // Álbumes iniciales
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(initialItems) { item ->
                Card(
                    modifier = Modifier
                        .size(120.dp)
                        .clickable {
                            viewModel.loadRecommendations(item.itemId)
                        },
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Recomendaciones
        when (state) {
            is MusicRecViewModel.UiState.Idle -> {}
            MusicRecViewModel.UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is MusicRecViewModel.UiState.Success -> {
                val list = (state as MusicRecViewModel.UiState.Success).list
                LazyColumn {
                    items(list) { rec ->
                        ListItem(
                            headlineContent = { Text(rec.title) },
                            supportingContent = { Text("Distancia: %.2f".format(rec.distance)) },
                            leadingContent = {
                                AsyncImage(
                                    model = rec.imageUrl,
                                    contentDescription = rec.title,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        )
                        HorizontalDivider(
                            Modifier,
                            DividerDefaults.Thickness,
                            DividerDefaults.color
                        )
                    }
                }
            }
            is MusicRecViewModel.UiState.Error -> {
                Text(
                    text = "Error: ${(state as MusicRecViewModel.UiState.Error).message}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
