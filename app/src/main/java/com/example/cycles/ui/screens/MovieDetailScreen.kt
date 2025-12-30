package com.example.cycles.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cycles.utils.TmdbImageUtils
import com.example.cycles.viewmodel.MovieDetailViewModel

@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            // Botón de atrás flotante y transparente
            SmallFloatingBackButton(onBackClick)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val movie = uiState.movie!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(bottom = innerPadding.calculateBottomPadding())
                ) {
                    // 1. HEADER CON IMAGEN DE FONDO
                    Box(modifier = Modifier.height(300.dp).fillMaxWidth()) {
                        // Backdrop Image
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(TmdbImageUtils.buildBackdropUrl(movie.backdropPath))
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Gradiente para que el texto se lea bien
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background),
                                        startY = 300f
                                    )
                                )
                        )

                        // Póster Pequeño superpuesto
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(TmdbImageUtils.buildPosterUrl(movie.posterPath))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Poster",
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(start = 16.dp, bottom = 0.dp) // Lo pegamos abajo
                                .width(100.dp)
                                .height(150.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }

                    // 2. INFORMACIÓN PRINCIPAL
                    Column(modifier = Modifier.padding(16.dp)) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Título
                        Text(
                            text = movie.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        if (movie.tagline != null) {
                            Text(
                                text = "\"${movie.tagline}\"",
                                style = MaterialTheme.typography.bodyMedium,
                                fontStyle = FontStyle.Italic,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Metadatos (Año | Duración | Rating)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = movie.releaseDate?.take(4) ?: "N/A", style = MaterialTheme.typography.labelLarge)
                            Text(" • ", modifier = Modifier.padding(horizontal = 4.dp))
                            Text(text = formatRuntime(movie.runtime), style = MaterialTheme.typography.labelLarge)
                            Text(" • ", modifier = Modifier.padding(horizontal = 4.dp))
                            Text(text = "★ ${movie.voteAverage}", color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Géneros (Chips)
                        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                            movie.genres.forEach { genre ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(genre) },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sinopsis
                        Text(
                            text = "Sinopsis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = movie.overview,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SmallFloatingBackButton(onClick: () -> Unit) {
    // Un botón circular pequeño para volver atrás
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .padding(top = 48.dp, start = 16.dp) // Margen para la barra de estado
            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Atrás",
            tint = Color.White
        )
    }
}

fun formatRuntime(minutes: Int): String {
    if (minutes <= 0) return "Duración desc."
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
}