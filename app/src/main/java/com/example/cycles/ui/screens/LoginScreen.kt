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
import com.example.cycles.ui.theme.AnimatedBackground // 🎯 IMPORTA TU COMPONENTE AQUÍ

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // ... (Estados y LaunchedEffect permanecen igual) ...

    val email by viewModel.usernameOrEmail.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->

        // 🎯 REEMPLAZAMOS Surface y Column con AnimatedBackground
        AnimatedBackground(
            // Aplicamos el padding del Scaffold al fondo
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column( // Usamos un Column interno para centrar el contenido
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp), // Añadimos padding horizontal para los campos
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::onUsernameOrEmailChange,
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
                    onClick = { viewModel.onLoginClick(navController) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Iniciar sesión")
                }
                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                    Text("¿Olvidaste tu contraseña?")
                }
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}