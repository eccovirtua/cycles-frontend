package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.LoginViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue //delegación de propiedades



@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel() // Obtén una instancia del ViewModel
) {
    //estados
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState() // Observa el estado isLoading del ViewModel
    val errorMsg by viewModel.error.collectAsState()

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Nombre de usuario o Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(24.dp))
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
            }
            Button(
                onClick = { viewModel.onLoginClick() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Iniciar sesión")
            }
            Spacer(Modifier.height(16.dp))
//            TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
//                Text("¿Olvidaste tu contraseña?")
//            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}