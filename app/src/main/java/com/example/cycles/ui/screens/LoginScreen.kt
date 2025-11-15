package com.example.cycles.ui.screens



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.LoginViewModel
import androidx.compose.ui.Alignment
import com.example.cycles.ui.components.CyclesPrimaryButton

@Composable
fun LoginScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val email by viewModel.usernameOrEmail.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val isFormValid = email.isNotEmpty() && password.isNotEmpty()



    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        // 1. Contenido Principal (Formulario)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 25.dp, vertical = 29.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(40.dp))

            Text(
                text= "Iniciar Sesión",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onUsernameOrEmailChange,
                shape = RoundedCornerShape(6.dp),
                placeholder = { Text("Nombre de usuario o correo", style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Icon(Icons.Filled.AlternateEmail, contentDescription = "Icono de correo")
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),

                modifier = Modifier.height(53.dp).fillMaxWidth() //altura del boton
            )
            Spacer(Modifier.height(7.dp)) //espacio entre ambos box


            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,

                shape = RoundedCornerShape(6.dp),
                placeholder = { Text("Contraseña",style = MaterialTheme.typography.bodySmall) },

                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = "Icono de contraseña")
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),

                modifier = Modifier.height(53.dp).fillMaxWidth(), //altura del boton
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(24.dp))
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
            }

            if (isLoading) {
                // Si está cargando, aún usamos el CircularProgressIndicator
                Button(
                    onClick = { /* No hacer nada si está cargando */ },
                    modifier = Modifier.fillMaxWidth().height(52.dp), // Mantener altura consistente
                    enabled = false
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                }
            } else {
                CyclesPrimaryButton(
                    text = "Iniciar sesión",
                    enabled = isFormValid,
                    onClick = { viewModel.onLoginClick(navController) },
                )
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

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
        )
    }
}
