package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cycles.viewmodel.ChooseUsernameViewModel

// 1. COMPONENTE STATEFUL (CON LÓGICA Y VIEWMODEL)
// Este es el que llamas desde tu NavigationGraph
@Composable
fun ChooseUsernameScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ChooseUsernameViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val isAvailable by viewModel.isAvailable.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Llamamos al componente "tonto" (Stateless) pasándole solo lo que necesita pintar
    ChooseUsernameContent(
        paddingValues = paddingValues,
        name = name,
        isAvailable = isAvailable,
        error = error,
        isLoading = isLoading,
        onNameChange = viewModel::onNameChange,
//        onCheckAvailability = { viewModel.checkAvailability() },
//        onSaveAndContinue = { viewModel.saveUsername(navController)
        }
    )
}

// 2. COMPONENTE STATELESS (PURO UI)
// Este no sabe qué es un ViewModel, solo recibe datos. ES PREVISUALIZABLE.
@Composable
fun ChooseUsernameContent(
    paddingValues: PaddingValues,
    name: String,
    isAvailable: Boolean?,
    error: String?,
    isLoading: Boolean,
    onNameChange: (String) -> Unit,
    onCheckAvailability: () -> Unit,
    onSaveAndContinue: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 25.dp, vertical = 29.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(40.dp))

            Text(
                text = "Nombre de usuario",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Elige un nombre de usuario para tu cuenta y perfil y verifica que esté disponible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                shape = RoundedCornerShape(6.dp),
                leadingIcon = {
                    Icon(Icons.Filled.AlternateEmail, contentDescription = "Icono de usuario")
                },
                placeholder = {
                    Text(
                        "Ingresa un nombre de usuario...",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier
                    .height(53.dp)
                    .fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // Botón Verificar
            Button(
                onClick = onCheckAvailability,
                enabled = name.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Verificar disponibilidad")
            }

            if (isLoading) {
                Spacer(Modifier.height(8.dp))
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            }

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error, color = MaterialTheme.colorScheme.error)
            }

            // Botón Continuar (Solo aparece si está disponible)
            if (isAvailable == true) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onSaveAndContinue,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text("Continuar")
                }
            }
        }
    }
}

// 3. PREVIEWS (AHORA SÍ FUNCIONAN)
// Creamos varios previews para ver los distintos estados de la UI

@Preview(showBackground = true, name = "1. Estado Inicial")
@Composable
fun PreviewChooseUsernameNormal() {
    MaterialTheme {
        ChooseUsernameContent(
            paddingValues = PaddingValues(0.dp),
            name = "",
            isAvailable = null,
            error = null,
            isLoading = false,
            onNameChange = {},
            onCheckAvailability = {},
            onSaveAndContinue = {}
        )
    }
}

@Preview(showBackground = true, name = "2. Cargando / Escribiendo")
@Composable
fun PreviewChooseUsernameLoading() {
    MaterialTheme {
        ChooseUsernameContent(
            paddingValues = PaddingValues(0.dp),
            name = "martin_dev",
            isAvailable = null,
            error = null,
            isLoading = true,
            onNameChange = {},
            onCheckAvailability = {},
            onSaveAndContinue = {}
        )
    }
}

@Preview(showBackground = true, name = "3. Disponible (Éxito)")
@Composable
fun PreviewChooseUsernameAvailable() {
    MaterialTheme {
        ChooseUsernameContent(
            paddingValues = PaddingValues(0.dp),
            name = "martin_dev_pro",
            isAvailable = true, // Simula que la API dijo "Sí"
            error = null,
            isLoading = false,
            onNameChange = {},
            onCheckAvailability = {},
            onSaveAndContinue = {}
        )
    }
}

@Preview(showBackground = true, name = "4. Error (No Disponible)")
@Composable
fun PreviewChooseUsernameError() {
    MaterialTheme {
        ChooseUsernameContent(
            paddingValues = PaddingValues(0.dp),
            name = "admin",
            isAvailable = false,
            error = "El usuario ya existe, intenta otro.",
            isLoading = false,
            onNameChange = {},
            onCheckAvailability = {},
            onSaveAndContinue = {}
        )
    }
}