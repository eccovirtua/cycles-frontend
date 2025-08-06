//package com.example.cycles.ui.screens
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import coil.compose.AsyncImage
//import com.example.cycles.data.RecommendationItem
//import com.example.cycles.viewmodel.MovieRecViewModel
//
//
//@Composable
//fun MovieRecScreen(
//    viewModel: MovieRecViewModel = hiltViewModel()
//) {
//    val state by viewModel.uiState.collectAsState()
//
//    // 3 películas/series semilla (reemplaza itemId y imageUrl por tus datos reales)
//    val initialMovies = listOf(
//        RecommendationItem(
//            itemId = "ml-1",  // ej. "ml-1" corresponde a la primera película en tu dataset
//            title = "Toy Story (1995)",
//            distance = 0.0,
//            imageUrl = "https://link_a_poster1.jpg"
//        ),
//        RecommendationItem(
//            itemId = "ml-2",
//            title = "The Godfather (1972)",
//            distance = 0.0,
//            imageUrl = "https://link_a_poster2.jpg"
//        ),
//        RecommendationItem(
//            itemId = "ml-3",
//            title = "The Dark Knight (2008)",
//            distance = 0.0,
//            imageUrl = "https://link_a_poster3.jpg"
//        )
//    )
//
//    Column(Modifier.fillMaxSize().padding(16.dp)) {
//        Text("¿Qué película o serie quieres ver hoy?", style = MaterialTheme.typography.headlineSmall)
//        Spacer(Modifier.height(8.dp))
//
//        // Carrusel de portadas iniciales
//        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            items(initialMovies) { movie ->
//                Card(
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clickable { viewModel.loadRecommendations(movie.itemId) },
//                    elevation = CardDefaults.cardElevation(2.dp)
//                ) {
//                    AsyncImage(
//                        model = movie.imageUrl,
//                        contentDescription = movie.title,
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        // Lista de recomendaciones
//        when (state) {
//            MovieRecViewModel.UiState.Idle -> { /* mensaje inicial opcional */ }
//            MovieRecViewModel.UiState.Loading -> {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//            }
//            is MovieRecViewModel.UiState.Success -> {
//                val recs = (state as MovieRecViewModel.UiState.Success).list
//                LazyColumn {
//                    items(recs) { rec ->
//                        ListItem(
//                            headlineContent = { Text(rec.title) },
//                            supportingContent = { Text("Distancia: %.2f".format(rec.distance)) },
//                            leadingContent = {
//                                AsyncImage(
//                                    model = rec.imageUrl,
//                                    contentDescription = rec.title,
//                                    modifier = Modifier.size(48.dp)
//                                )
//                            }
//                        )
//                        HorizontalDivider()
//                    }
//                }
//            }
//            is MovieRecViewModel.UiState.Error -> {
//                Text(
//                    text = "Error: ${(state as MovieRecViewModel.UiState.Error).message}",
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}