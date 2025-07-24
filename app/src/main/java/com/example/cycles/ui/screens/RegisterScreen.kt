package com.example.cycles.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.RegisterViewModel
import com.example.cycles.ui.components.DateOfBirthPicker

@Composable
fun RegisterScreen(
    navController: NavHostController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // Estados
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()
    val dob by viewModel.dateOfBirth.collectAsState() // Estado de la fecha de nacimiento del ViewModel

    val snackbarHostState = remember { SnackbarHostState() } //snackbar

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            val token = viewModel.jwtToken.value
                ?: return@collect

            Log.d("NAV_DEBUG", "Navegando a: choose_username/$token")
            navController.navigate(Screen.ChooseUsername.createRoute(token))

        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // El anclaje del Snackbar
        modifier = Modifier.fillMaxSize() // El Scaffold ocupa toda la pantalla
    ) { paddingValues -> // paddingValues es crucial para que el contenido no se solape con el Snackbar
        // El Surface ahora va dentro del contenido del Scaffold

        Surface(
            modifier = Modifier.fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Ingresa tu correo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = viewModel::onPasswordChange,
                    label = { Text("Ingresa tu contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(Modifier.height(24.dp))

                DateOfBirthPicker(
                    selectedDate = dob, // Pasa la fecha actual del ViewModel
                    onDateSelected = { newDate ->
                        viewModel.updateDateOfBirth(newDate) // El callback actualiza el ViewModel
                    },
                    modifier = Modifier.fillMaxWidth() // Asegúrate de que ocupe el ancho
                )
                Spacer(Modifier.height(24.dp))

                if (errorMsg.isNotEmpty()) {
                    Text(errorMsg, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                }
                Button(
                    onClick = { viewModel.onRegisterClick() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    else Text("Registrarse")
                }
                Spacer(Modifier.height(16.dp))

                TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                    Text("¿Ya tienes cuenta? Inicia sesión")
                }
            }
        }
    }
}