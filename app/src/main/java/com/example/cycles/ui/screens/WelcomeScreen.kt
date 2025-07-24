package com.example.cycles.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.cycles.R
import com.example.cycles.ui.theme.CyclesTheme
import com.example.cycles.viewmodel.WelcomeViewModel
import androidx.compose.ui.unit.sp // Importa sp para el tamaño de fuente

@Composable
fun WelcomeScreen(
    navController: NavHostController,
    viewModel: WelcomeViewModel = hiltViewModel()
) {
    // 1) Estado de carga
    val isReady by viewModel.isScreenReady.collectAsState()
    // 2) Mensaje de bienvenida
    val message by viewModel.welcomeMessage.collectAsState()

    //colores de fuente para el texto
    val gradientColors = listOf(Color.Red, Color.Magenta, Color.Red)

    if (!isReady) {
        // Indicador de carga
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // UI principal
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "App Logo",
                    modifier = Modifier.size(120.dp)

                )

                // Mensaje dinamico
                Text(
                    text = message,
                    style = TextStyle(color = Color.White, fontSize = 50.sp,
                        fontWeight = FontWeight.Bold).copy(
                        brush = Brush.linearGradient(colors = gradientColors)
                    ),
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                // Botones
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { navController.navigate("login") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Iniciar sesión")
                    }

                    Spacer(modifier = Modifier.height(20.dp))//espacio entre los dos botones de login/register

                    Button(
                        onClick = { navController.navigate("register") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrarse")
                    }

                    Spacer(modifier = Modifier.height(20.dp)) //este space innecesario por si en el futuro se deben ageregar elemtnos a la WelcomeScreen

                }
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    CyclesTheme(dynamicColor = false, darkTheme = true) {
        // Creamos un NavHostController de prueba
        val fakeNavController = rememberNavController()
        // Creamos directamente el ViewModel (sin Hilt) para el preview
        val fakeViewModel = WelcomeViewModel()
        WelcomeScreen(
            navController = fakeNavController,
            viewModel = fakeViewModel
        )
    }
}
