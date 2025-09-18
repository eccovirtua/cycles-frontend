package com.example.cycles.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    LaunchedEffect(domain) {
        viewModel.createSession(domain)
    }

    when (val state = uiState) {
        is InteractiveRecViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is InteractiveRecViewModel.UiState.Error -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Error: ${state.message}")
                Button(onClick = { viewModel.createSession(domain) }) {
                    Text("Reintentar")
                }
            }
        }
        is InteractiveRecViewModel.UiState.Seed -> {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(model = state.seed.imageUrl, contentDescription = state.seed.title)
                Text(state.seed.title)
                Row {
                    Button(onClick = { viewModel.sendFeedback(1) }) { Text("👍") }
                    Button(onClick = { viewModel.sendFeedback(0) }) { Text("👎") }
                }
            }
        }
        is InteractiveRecViewModel.UiState.Final -> {
            // Aquí navegas a la pantalla final cuando ya tienes la lista
            LaunchedEffect(Unit) {
                navController.navigate("final/$domain/${viewModel.sessionId}") {
                    popUpTo("home") { inclusive = false }
                }
            }
        }
    }
}