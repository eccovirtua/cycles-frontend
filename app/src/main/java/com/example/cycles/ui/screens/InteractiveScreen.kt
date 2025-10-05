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


@Composable
fun InteractiveRecScreen(
    domain: String,
    navController: NavController,
    viewModel: InteractiveRecViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(domain) {
        val sessionId = SessionCache.getSession(domain)
        val wasReset = SessionCache.isSessionReset(domain)

        if (sessionId == null || wasReset) {
            // Crear una nueva sesión
            viewModel.createSession(domain)
            SessionCache.clearSessionResetFlag(domain)
        } else {
            // Verificar estado de la sesión existente
            try {
                val state = viewModel.getSessionState(sessionId)
                if (state.finished) {
                    // Si la sesión ya terminó → ir al grid final
                    navController.navigate("final/$domain/$sessionId") {
                        popUpTo("home") { inclusive = false }
                    }
                } else {
                    // Si no terminó → continuar desde el último seed
                    viewModel.resumeSession(sessionId)
                    state.last_item?.let {
                        viewModel.loadExistingSeed(it, state.iterations)
                    }
                }
            } catch (e: Exception) {
                // Si falla la consulta, crear nueva sesión
                viewModel.createSession(domain)
            }
        }
    }

    when (val state = uiState) {
        is InteractiveRecViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is InteractiveRecViewModel.UiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Error: ${state.message}")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.createSession(domain) }) {
                    Text("Reintentar")
                }
            }
        }
        is InteractiveRecViewModel.UiState.Seed -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = state.seed.imageUrl,
                    contentDescription = state.seed.title,
                    modifier = Modifier.size(240.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(state.seed.title, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = { viewModel.sendFeedback(1) }) { Text("👍") }
                    Button(onClick = { viewModel.sendFeedback(0) }) { Text("👎") }
                }
            }
        }
        is InteractiveRecViewModel.UiState.Final -> {
            navController.navigate("final/${domain}/${viewModel.sessionId}")
        }
    }
}