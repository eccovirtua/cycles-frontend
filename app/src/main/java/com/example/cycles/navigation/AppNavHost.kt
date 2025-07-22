package com.example.cycles.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cycles.ui.screens.WelcomeScreen


//defines las rutas en código con un NavHost. AppNavHost es tu “mapa” de pantallas.
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "welcome") {
        //ruta de pantalla principal
        composable("welcome") {
            WelcomeScreen(navController)
        }
        // Aquí irán login, register y forgot_password más adelante
    }
}
