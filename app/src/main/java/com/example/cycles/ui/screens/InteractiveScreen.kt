package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cycles.viewmodel.InteractiveRecViewModel


@Composable
fun InteractiveRecScreen(
    domain: String,
    viewModel: InteractiveRecViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Carga inicial
    LaunchedEffect(Unit) {
        viewModel.loadInitialSeed(domain)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        when (state) {
            InteractiveRecViewModel.UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is InteractiveRecViewModel.UiState.Success -> {
                val seed = (state as InteractiveRecViewModel.UiState.Success).seed
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = seed.imageUrl,
                        contentDescription = seed.title,
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp)
                    )
                    Text(
                        text = seed.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(onClick = { viewModel.sendFeedback( -1) }) {
                            Text("No me gusta")
                        }
                        TextButton(onClick = {
                            viewModel.resetRecommendations()
                        }) {
                            Text("Reiniciar")
                        }
                        Button(onClick = { viewModel.sendFeedback(+1) }) {
                            Text("Me gusta")
                        }
                    }
                }
            }
            is InteractiveRecViewModel.UiState.Error -> {
                val msg = (state as InteractiveRecViewModel.UiState.Error).message
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: $msg", color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}