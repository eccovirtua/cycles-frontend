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
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first

@Composable
fun HomeScreen (
    navController: NavHostController
) {

    val authViewModel: AuthViewModel = hiltViewModel()
    val viewModel: HomeViewModel = hiltViewModel()

    // Flag para saber si ya consumimos la primera emisión de isValid(que es el valor inicial) y solo reaccionar a cambios posteriores





//    val isLoading by viewModel.isLoading.collectAsState()
    //val errorMsg by viewModel.error.collectAsState()



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