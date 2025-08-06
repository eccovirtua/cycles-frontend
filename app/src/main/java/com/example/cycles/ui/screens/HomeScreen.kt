package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.AuthViewModel
import com.example.cycles.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen (
    navController: NavHostController
) {

    val authViewModel: AuthViewModel = hiltViewModel()
    val viewModel: HomeViewModel = hiltViewModel()



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
            TextButton(
                onClick = {
                    viewModel.onLogoutClick(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
                ) { Text("Cerrar sesión") }

            Spacer(Modifier.height(11.dp)) //espacio entre los dos botones
            TextButton(
                onClick = {
                    navController.navigate("interactive_music")
                }
            ) {
                Text("Ver recomendaciones de música")
            }

            Spacer(Modifier.height(11.dp)) //espacio para el boton de rec de libros
            TextButton(
                onClick = {
                    navController.navigate("interactive_books")
                }
            ) {
                Text("Ver las recomendaciones de libros")
            }


            Spacer(Modifier.height(11.dp)) //espacio para el boton de rec de libros (verificar ya q no se si funcoina esta meirda)
            TextButton(
                onClick = {
                    navController.navigate("interactive_movies")

                }
            ) {
                Text("Ver las recomendaciones de películas/shows")
            }





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