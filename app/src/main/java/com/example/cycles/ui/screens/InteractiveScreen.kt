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
import com.example.cycles.viewmodel.SessionCache
import kotlinx.coroutines.launch

@Composable
fun InteractiveRecScreen(
    domain: String,
    navController: NavController,
    viewModel: InteractiveRecViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // carga inicial
    LaunchedEffect(domain) {
        if (viewModel.navigatingToFinalGrid) return@LaunchedEffect

        val existing = SessionCache.getSession(domain)
        if (existing != null) {
            viewModel.navigatingToFinalGrid = true
            navController.navigate("final/$domain/$existing") {
                popUpTo("home") { inclusive = false }
            }
        } else {
            viewModel.loadInitialSeed(domain) { finalList ->
                scope.launch {
                    val sessionId = viewModel.sessionId
                    if (sessionId != null) {
                        SessionCache.saveSession(domain, sessionId)
                        viewModel.navigatingToFinalGrid = true
                        navController.navigate("final/$domain/$sessionId") {
                            popUpTo("home") { inclusive = false }
                        }
                    }
                }
            }
        }
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
                Button(onClick = {
                    viewModel.loadInitialSeed(domain) { finalList ->
                        scope.launch {
                            val sessionId = viewModel.sessionId
                            if (sessionId != null) {
                                navController.navigate("final/$domain/$sessionId") {
                                    popUpTo("home") { inclusive = false }
                                }
                            }
                        }
                    }
                }) {
                    Text("Reintentar")
                }
            }
        }

        is InteractiveRecViewModel.UiState.Seed -> {
            val seed = (uiState as InteractiveRecViewModel.UiState.Seed).seed

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = seed.imageUrl,
                    contentDescription = seed.title,
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(seed.title, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(onClick = {
                        viewModel.sendFeedback(
                            feedback = 1,
                            onSuccess = { finalList ->
                                val sessionId = viewModel.sessionId
                                if (sessionId != null) {
                                    navController.navigate("final/$domain/$sessionId") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            }
                        )
                    }) {
                        Text("👍")
                    }

                    Button(onClick = {
                        viewModel.sendFeedback(
                            feedback = 0,
                            onSuccess = { finalList ->
                                val sessionId = viewModel.sessionId
                                if (sessionId != null) {
                                    navController.navigate("final/$domain/$sessionId") {
                                        popUpTo("home") { inclusive = false }
                                    }
                                }
                            }
                        )
                    }) {
                        Text("👎")
                    }
                }
            }
        }
    }
}