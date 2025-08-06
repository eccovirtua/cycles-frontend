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
//import com.example.cycles.viewmodel.BookRecViewModel
//
//
//@Composable
//fun BookRecScreen(
//    viewModel: BookRecViewModel = hiltViewModel()
//) {
//    val state by viewModel.uiState.collectAsState()
//
//    // 3 libros semilla
//    val initialBooks = listOf(
//        RecommendationItem(
//            itemId = "gb-icKmd-tlvPMC",
//            title = "Journey to the Center of the Earth",
//            distance = 0.0,
//            imageUrl = "http://books.google.com/books/content?id=XdMBT"
//        ),
//        RecommendationItem(
//            itemId = "gb-UiRdEQAAQBAJ",
//            title = "The History of the Peloponnesian War",
//            distance = 0.0,
//            imageUrl = "https://…/hobbit.jpg"
//        ),
//        RecommendationItem(
//            itemId = "gb-6vGiDwAAQBAJ",
//            title = "The Picture of Dorian Gray",
//            distance = 0.0,
//            imageUrl = "https://…/charlottes_web.jpg"
//        )
//    )
//
//    Column(Modifier.fillMaxSize().padding(16.dp)) {
//        Text("¿Qué libro quieres leer hoy?", style = MaterialTheme.typography.headlineSmall)
//        Spacer(Modifier.height(8.dp))
//
//        // Carrusel de portadas iniciales
//        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
//            items(initialBooks) { book ->
//                Card(
//                    modifier = Modifier
//                        .size(120.dp)
//                        .clickable { viewModel.loadRecommendations(book.itemId) },
//                    elevation = CardDefaults.cardElevation(2.dp)
//                ) {
//                    AsyncImage(
//                        model = book.imageUrl,
//                        contentDescription = book.title,
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
//            BookRecViewModel.UiState.Idle -> { /* mensaje inicial opcional */ }
//            BookRecViewModel.UiState.Loading -> {
//                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//            }
//            is BookRecViewModel.UiState.Success -> {
//                val recs = (state as BookRecViewModel.UiState.Success).list
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
//            is BookRecViewModel.UiState.Error -> {
//                Text(
//                    text = "Error: ${(state as BookRecViewModel.UiState.Error).message}",
//                    color = MaterialTheme.colorScheme.error
//                )
//            }
//        }
//    }
//}