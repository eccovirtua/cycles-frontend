package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cycles.viewmodel.InteractiveRecViewModel

@Composable
fun InteractiveRecScreen(
    domain: String,
    navController: NavController,
    viewModel: InteractiveRecViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Cargar seed inicial
    LaunchedEffect(domain) {
        viewModel.loadInitialSeed(domain)
    }

    when (uiState) {
        is InteractiveRecViewModel.UiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is InteractiveRecViewModel.UiState.Error -> {
            val message = (uiState as InteractiveRecViewModel.UiState.Error).message
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: $message")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.loadInitialSeed(domain) }) {
                    Text("Reintentar")
                }
            }
        }

        is InteractiveRecViewModel.UiState.Success -> {
            val seed = (uiState as InteractiveRecViewModel.UiState.Success).seed

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Portada del item
                AsyncImage(
                    model = seed.imageUrl,
                    contentDescription = seed.title,
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(seed.title, style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(24.dp))

                // Botones de feedback
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = { viewModel.sendFeedback(1) }) {
                        Text("👍")
                    }
                    Button(onClick = { viewModel.sendFeedback(0) }) {
                        Text("👎")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de reset
                Button(onClick = { viewModel.resetRecommendations() }) {
                    Text("Resetear Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de finalizar
                Button(
                    onClick = {
                        viewModel.finalizeSession { sessionId ->
                            // Pasamos la lista al siguiente destino
                            navController.currentBackStackEntry
                            navController.navigate("final_recs/$sessionId")
                        }
                    }
                ) {
                    Text("Finalizar Sesión")
                }
            }
        }
    }
}
