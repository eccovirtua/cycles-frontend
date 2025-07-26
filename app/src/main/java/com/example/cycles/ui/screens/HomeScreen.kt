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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.HomeViewModel
import com.example.cycles.viewmodel.AuthViewModel

@Composable
fun HomeScreen (
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel(), //este viewmodel adicional para la verificacion en segundo plano de la autenticacion existente (token)
    viewModel: HomeViewModel = hiltViewModel() //importante instanciar siempre Hilt ya que los viewModel son Hilt
) {

    val isValid by authViewModel.isTokenValid.collectAsState(initial = true)

    // Flag para saber si ya consumimos la primera emisión de isValid(que es el valor inicial) y solo reaccionar a cambios posteriores

    var hasChecked by remember { mutableStateOf(false) }



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
                onClick = { viewModel.onLogoutClick(navController) },
//              modifier = Modifier.fillMaxWidth() no se necesita que el boton cubra toda la puta pantalla
                ) {
                    Text("Cerrar sesión")
                }
        }
    }



    //en segundo plano, en cuanto isValid sea false, tira de una al welcome (login/reg)
    LaunchedEffect(isValid) {

        // Ignora la primera vez que entra aquí:
        if (!hasChecked) {
            hasChecked = true
            return@LaunchedEffect
        }
        if (!isValid)
            navController.navigate(Screen.Welcome.route) {
                popUpTo(Screen.Home.route) {inclusive = true} //siempre usar popupto para borrar la pila de pantallas y no volver si no hay autentication (logica)
            }
    }
}