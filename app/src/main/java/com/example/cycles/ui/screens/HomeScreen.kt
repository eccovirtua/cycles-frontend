package com.example.cycles.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.cycles.ui.components.SectionCard
import com.example.cycles.viewmodel.AuthViewModel
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

    // 🎯 1. Define la acción de click para el cambio de tema (como en tu WelcomeScreen)
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

        // ✅ CORRECCIÓN 1: Necesitamos un CoroutineScope para envolver la acción pesada
        val scope = rememberCoroutineScope()

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        var isTapping by remember { mutableStateOf(false) }


        val scaleFactor by animateFloatAsState(
            // La animación de escala usa isPressed para el rebote visual
            targetValue = if (isPressed || isTapping) 0.85f else 1.0f,
            // ✅ CAMBIO CLAVE: Reducimos la duración a 50ms para un toque instantáneo
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
                                // 🎯 RETRASO CLAVE: Permite que la animación de isPressed=false se complete
                                delay(150)
                                themeCycleAction()
                            }
                        }
                )
            )
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            // 1. Aplica el padding del Scaffold (Top/Bottom Bar)
            .padding(paddingValues)

            .verticalScroll(scrollState)

            // ✅ CORRECCIÓN CLAVE: Separar el padding horizontal del vertical/específico.
            .padding(horizontal = 26.dp) // Aplica 25dp a start y end
            .padding(top = 75.dp),      // Aplica 40dp solo a top (y 0 a bottom, ya cubierto por paddingValues)

        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        // 1. TÍTULO
        CyclesTitleComposable(themeCycleAction = themeCycleAction)

        Spacer(Modifier.height(20.dp))

        // 2. SUBTÍTULO
        Text(
            text = "¿Qué quieres planear para hoy?",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(15.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp)) // Espacio entre el subtítulo y las tarjetas

            // Tarjeta 1: Películas/TV
            SectionCard(
                title = "Películas",
                icon = Icons.Filled.LocalMovies,
                onClick = { navController.navigate("interactive_movies") },
                color1 = Color(0xFF6A1B9A),
                color2 = Color(0xFFFF5252),
                modifier = Modifier.fillMaxWidth()
            )

            // Tarjeta 2: Libros
            SectionCard(
                title = "Libros",
                icon = Icons.Filled.Book,
                color1 = Color(0xFF43A047),
                color2 = Color(0xFFDCE775),
                onClick = { navController.navigate("interactive_books") },
                modifier = Modifier.fillMaxWidth()
            )

            // Tarjeta 3: Música
            SectionCard(
                title = "Música",
                icon = Icons.Filled.MusicNote,
                color1 = Color(0xFF1976D2),
                color2 = Color(0xFF00E5FF),
                onClick = { navController.navigate("interactive_music") },
                modifier = Modifier.fillMaxWidth()
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