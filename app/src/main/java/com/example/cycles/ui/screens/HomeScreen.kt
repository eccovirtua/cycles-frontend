package com.example.cycles.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.example.cycles.ui.components.SectionCard
import com.example.cycles.viewmodel.AuthViewModel
import com.example.cycles.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun HomeScreen (
    navController: NavHostController,
    paddingValues: PaddingValues,
    onTitleClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val authViewModel: AuthViewModel = hiltViewModel()

    
    val themeCycleAction: () -> Unit = {
        onTitleClick()

        println("¡Cambiando el tema de la aplicación!")
    }


    @Composable
    fun CyclesTitleComposable(themeCycleAction: () -> Unit) {

        // Estilo base para ambos textos
        val baseStyle = TextStyle(
            fontSize = 39.sp,
            fontWeight = FontWeight.Bold
        )

        // Estilo del degradado (para la palabra "Cycles")
        val gradientBrush = Brush.linearGradient(
            colors = listOf(Color.Red, Color.Magenta, Color.Red)
        )

        val scope = rememberCoroutineScope()

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        var isTapping by remember { mutableStateOf(false) }


        val scaleFactor by animateFloatAsState(
            // La animación de escala usa isPressed para el rebote visual
            targetValue = if (isPressed || isTapping) 0.85f else 1.0f,
            animationSpec = tween(durationMillis = 150),
            label = "scale_animation"
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Texto Fijo: "Bienvenido a "
            Text(
                text = "Bienvenido a ",
                style = baseStyle.copy(color = MaterialTheme.colorScheme.onBackground)
            )

            // 2. Texto Clickeable: "Cycles" con Degradado
            Text(
                text = "Cycles",
                style = baseStyle.copy(
                    // Aplicamos el degradado (Brush)
                    brush = gradientBrush
                ),
                modifier = Modifier
                    .scale(scaleFactor)
                    .clickable(interactionSource = interactionSource, // Le damos el InteractionSource
                    indication = null, // Y ahora sí permitimos indication = null
                        onClick = {
                            scope.launch {
                                
                                delay(150)
                                themeCycleAction()
                            }
                        }
                )
            )
        }
    }

    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.uiState.collectAsState()
    val isLimitReached by homeViewModel.isLimitReached.collectAsState()

    // Recargar el estado de uso cuando la pantalla se vuelve visible
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            homeViewModel.loadUsageStatus()
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            // 1. Aplica el padding del Scaffold (Top/Bottom Bar)
            .padding(paddingValues)

            .verticalScroll(scrollState)

            .padding(horizontal = 26.dp) // Aplica 25dp a start y end
            .padding(top = 55.dp),      // Aplica 40dp solo a top (y 0 a bottom, ya cubierto por paddingValues)

        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        // 1. TÍTULO
        CyclesTitleComposable(themeCycleAction = themeCycleAction)

        Spacer(Modifier.height(19.dp))

        // 2. SUBTÍTULO
        Text(
            text = "Mejora tu bienestar planificando rutinas de entretenimiento",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(22.dp))
        RemainingSessionsCard(remaining = homeState.remainingSessions)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(2.dp)) // Espacio entre el subtítulo y las tarjetas

            // Tarjeta 1: Películas/TV
            SectionCard(
                title = "Películas",
                icon = Icons.Filled.LocalMovies,
                onClick = { navController.navigate("interactive_movies") },
                color1 = Color(0xFF6A1B9A),
                color2 = Color(0xFFFF5252),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLimitReached
            )

            // Tarjeta 2: Libros
            SectionCard(
                title = "Libros",
                icon = Icons.Filled.Book,
                color1 = Color(0xFF43A047),
                color2 = Color(0xFFDCE775),
                onClick = { navController.navigate("interactive_books") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLimitReached
            )

            // Tarjeta 3: Música
            SectionCard(
                title = "Música",
                icon = Icons.Filled.MusicNote,
                color1 = Color(0xFF1976D2),
                color2 = Color(0xFF00E5FF),
                onClick = { navController.navigate("interactive_music") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLimitReached
            )

            // ✅ 2. Tarjeta 4: Dashboard (NUEVA)
            SectionCard(
                title = "Dashboard",
                icon = Icons.Filled.Dashboard,
                color1 = Color(0xFFFFC107), // Color ámbar
                color2 = Color(0xFFFF6F00), // Color ámbar oscuro
                onClick = { navController.navigate("dashboard") }, // Navega a la nueva pantalla
                modifier = Modifier.fillMaxWidth(),
                enabled = true
            )


            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Verifica si el token está vacío y redirige al welcome
    LaunchedEffect(Unit) {
        val token = authViewModel.rawTokenFlow.first()
        // Esto suspende hasta que DataStore emita su primer valor real

        if (token.isNullOrEmpty()) {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(Screen.Home.route) { inclusive = true }
            }
        }
    }
}
@Composable
fun RemainingSessionsCard(remaining: Int?) {
    // 1. Animación de "flote" vertical
    val infiniteTransition = rememberInfiniteTransition(label = "floating_anim")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f, // Sube 4dp
        targetValue = 6f,  // Baja 4dp
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset_y"
    )

    // 2. Animación de color del texto (Blanco a Rojo)
    val textColor by animateColorAsState(
        targetValue = if (remaining == 0) Color.Red else Color.White,
        animationSpec = tween(durationMillis = 500),
        label = "text_color_anim"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp) // Altura de la tarjeta
            .graphicsLayer { translationY = offsetY }, // Aplica el desplazamiento vertical
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            // Fondo semitransparente
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Sesiones disponibles para hoy: ",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f) // Texto descriptivo más tenue
                )
                // Muestra el número o "--" si aún no se ha cargado
                Text(
                    text = remaining?.toString() ?: "--",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = textColor, // Color animado
                    fontSize = 30.sp // Tamaño grande para el número
                )
            }
        }
    }
}