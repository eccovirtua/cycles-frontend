package com.example.cycles.ui.screens

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.RegisterViewModel
import com.example.cycles.ui.components.DateOfBirthPicker
import androidx.compose.ui.Alignment // Necesario para el Box/Snackbar si lo usas, pero no aquí.

// 🎯 CLAVE 1: Debe recibir paddingValues
@Composable
fun RegisterScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    // Estados
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()
    val dob by viewModel.dateOfBirth.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect {
            val token = viewModel.jwtToken.value
                ?: return@collect

            Log.d("NAV_DEBUG", "Navegando a: choose_username/$token")
            navController.navigate(Screen.ChooseUsername.createRoute(token))

        }
    }


    Box(modifier = Modifier.fillMaxSize()) {

        // Contenedor principal: Column
        Column(
            // 🎯 CLAVE 3: Aplicar el paddingValues y el padding de diseño.
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 1.dp), // Ajustado a 24.dp para consistencia con LoginScreen
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(40.dp))


            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp)) //separacion de titulo y subtitulo

            Text(
                text = "Ingresa tus datos para crear tu cuenta en Cycles",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )


            Spacer(Modifier.height(10.dp))
            // TEXTO DE EMAIL ETIQUETA ARRIBA DEL CAMPO
            Text(
                text = "Correo",
                style = MaterialTheme.typography.labelLarge, // Estilo apropiado para etiquetas de formulario
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp)) // Espacio mínimo entre la etiqueta y el campo

            // Campo de Correo
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,

                shape = RoundedCornerShape(6.dp),
                placeholder = { Text("Ingresa tu correo",style = MaterialTheme.typography.bodySmall) },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Icono de correo")
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),

                modifier = Modifier.height(53.dp).fillMaxWidth() //altura del boton
            )


            Spacer(Modifier.height(12.dp)) //espacio entre correo y password

            // OutlinedTextField Password
            // Etiqueta "Contraseña"
            Text(
                text = "Contraseña",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp)) // Espacio mínimo entre la etiqueta y el campo

            // Campo de Contraseña (OutlinedTextField Personalizado)
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,

                shape = RoundedCornerShape(6.dp),
                placeholder = { Text("Ingresa tu contraseña",style = MaterialTheme.typography.bodySmall) },

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
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Fecha de nacimiento",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))

            // DateOfBirthPicker
            DateOfBirthPicker(
                selectedDate = dob,
                onDateSelected = { newDate ->
                    focusManager.clearFocus()
                    viewModel.updateDateOfBirth(newDate)
                },
                modifier = Modifier.fillMaxWidth(),

                )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Al crear tu cuenta admites ser mayor de edad y estar de acuerdo con los términos y condiciones.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(18.dp))


            // Mensaje de Error
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
            }

            // Botón Registrarse
            Button(
                onClick = viewModel::onRegisterClick,
                shape = RoundedCornerShape(25),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text("Siguiente")
            }
            Spacer(Modifier.height(16.dp))

            // Botón Iniciar Sesión
            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }

        // 🎯 CLAVE 4: SnackbarHost alineado al fondo y respetando el padding.
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(paddingValues)
        )
    }
}