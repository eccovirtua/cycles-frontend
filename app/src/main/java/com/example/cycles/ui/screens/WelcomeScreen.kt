package com.example.cycles.ui.screens


//imports para el preview
//import android.annotation.SuppressLint
//import androidx.compose.ui.tooling.preview.Preview
//import com.example.cycles.ui.theme.CyclesTheme
//import androidx.navigation.compose.rememberNavController

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.R
import com.example.cycles.viewmodel.WelcomeViewModel
import androidx.compose.ui.unit.sp // Importa sp para el tamaño de fuente
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.AuthViewModel

@Composable
fun WelcomeScreen(
    navController: NavHostController,
    welcomeViewModel: WelcomeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {

    //Observa el token (null = aún no leído o no existe) Si token es no‑nulo y no vacío, das por logueado al usuario.
    //La UI reacciona en tiempo real a cualquier cambio de tokenFlow.
    val token by authViewModel.tokenFlow.collectAsState(initial = null)

    // 2) Mensaje de bienvenida
    val message by welcomeViewModel.welcomeMessage.collectAsState()

    // 3) Cuando token sea distinto de null y no vacío, navega a Home
    LaunchedEffect(token) {
        if (!token.isNullOrEmpty()) {
            navController.navigate(Screen.Home.route) {
                popUpTo(route = Screen.Welcome.route) { inclusive = true }
            }
        }
    }



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

            // Mensaje dinámico con gradiente
            Text(
                text = message,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold
                ).copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Red, Color.Magenta, Color.Red)
                    )
                ),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Botones
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Button(
                    onClick = { navController.navigate(Screen.Login.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Iniciar sesión")
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate(Screen.Register.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}
