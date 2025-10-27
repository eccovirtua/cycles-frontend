package com.example.cycles.ui.screens
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
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
            } catch (_: Exception) {
                // Si falla la consulta, crear nueva sesión
                viewModel.createSession(domain)
            }
        }
    }
    // Variable para controlar la alerta
    var showLimitAlert by remember { mutableStateOf(false) }
    var limitAlertMessage by remember { mutableStateOf("") }

    if (showLimitAlert) {
        AlertDialog(
            onDismissRequest = {
                showLimitAlert = false
                // Volver a la pantalla anterior al cerrar la alerta
                navController.popBackStack()
            },
            title = { Text("Límite Alcanzado") },
            text = { Text(limitAlertMessage) },
            confirmButton = {
                Button(onClick = {
                    showLimitAlert = false
                    navController.popBackStack()
                }) {
                    Text("Entendido")
                }
            }
        )
    }

    when (val state = uiState) {
        is InteractiveRecViewModel.UiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // 🎯 MANEJAR EL NUEVO ESTADO DE ERROR
        is InteractiveRecViewModel.UiState.ErrorLimitReached -> {
            // Mostramos la alerta en lugar del error genérico
            LaunchedEffect(state.message) {
                limitAlertMessage = state.message
                showLimitAlert = true
            }
            // Muestra un Box vacío o un indicador mientras la alerta está visible
            Box(Modifier.fillMaxSize()) { /* Puedes poner un CircularProgressIndicator si quieres */ }
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
                    modifier = Modifier.size(470.dp)
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(state.seed.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(45.dp))
                // --- Updated Button Row ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly, // Keeps spacing
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // --- Dislike Button ---
                    val dislikeInteractionSource = remember { MutableInteractionSource() }
                    val isDislikePressed by dislikeInteractionSource.collectIsPressedAsState()
                    val dislikeScale by animateFloatAsState(
                        targetValue = if (isDislikePressed) 0.8f else 1.0f, // Shrink when pressed
                        label = "dislikeScaleAnim"
                    )

                    IconButton(
                        onClick = { viewModel.sendFeedback(-1) },
                        modifier = Modifier.graphicsLayer { // Apply scale animation
                            scaleX = dislikeScale
                            scaleY = dislikeScale
                        },
                        interactionSource = dislikeInteractionSource // Link interaction source
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ThumbDown,
                            contentDescription = "No me interesa",
                            modifier = Modifier.size(48.dp), // Larger icon size
                            tint = MaterialTheme.colorScheme.error // Optional: Color tint
                        )
                    }

                    // --- Randomize Button (Existing) ---
                    IconButton(onClick = { viewModel.randomizeSeed() }) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Siguiente item aleatorio",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    // --- Like Button ---
                    val likeInteractionSource = remember { MutableInteractionSource() }
                    val isLikePressed by likeInteractionSource.collectIsPressedAsState()
                    val likeScale by animateFloatAsState(
                        targetValue = if (isLikePressed) 0.8f else 1.0f, // Shrink when pressed
                        label = "likeScaleAnim"
                    )

                    IconButton(
                        onClick = { viewModel.sendFeedback(1) },
                        modifier = Modifier.graphicsLayer { // Apply scale animation
                            scaleX = likeScale
                            scaleY = likeScale
                        },
                        interactionSource = likeInteractionSource // Link interaction source
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ThumbUp,
                            contentDescription = "Me interesa",
                            modifier = Modifier.size(48.dp), // Larger icon size
                            tint = MaterialTheme.colorScheme.primary // Optional: Color tint
                        )
                    }
                }
                Spacer(modifier = Modifier.height(125.dp))
            }
        }
        is InteractiveRecViewModel.UiState.Final -> {
            navController.navigate("final/${domain}/${viewModel.sessionId}")
        }
    }
}