package com.example.cycles.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.cycles.ui.screens.LoginScreen
import com.example.cycles.ui.screens.RegisterScreen
import com.example.cycles.ui.screens.WelcomeScreen
import com.example.cycles.ui.screens.ForgotPasswordScreen


//defines las rutas en código con un NavHost. AppNavHost es tu “mapa” de pantallas.
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        //ruta de pantalla principal
        composable("welcome") {
            WelcomeScreen(navController)
        }

        //login
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        //register
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        //forgot password
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController) }

        }
}
