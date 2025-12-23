package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cycles.ui.components.DateOfBirthPicker
import com.example.cycles.viewmodel.ChooseUsernameViewModel


@Composable
fun ChooseUsernameScreen(
    navController: NavController,
    paddingValues: PaddingValues,
    viewModel: ChooseUsernameViewModel = hiltViewModel()
) {
    // Estados observados del ViewModel
    val name by viewModel.name.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()

    val loaded = remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    val dob by viewModel.dateOfBirth.collectAsState()


    LaunchedEffect(Unit) {
        if (!loaded.value) {
            val previousBackStack = navController.previousBackStackEntry
            val savedHandle = previousBackStack?.savedStateHandle

            if (savedHandle != null) {
                val email = savedHandle.get<String>("email")
                val password = savedHandle.get<String>("password")
                val age = savedHandle.get<Int>("age")

                // 2. INYECTARLOS AL VIEWMODEL
                viewModel.setRegistrationData(email, password, age)
                loaded.value = true
            }
        }
    }

    val showAgeInput by viewModel.showAgeInput.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 25.dp, vertical = 29.dp),
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(Modifier.height(40.dp))

            // Título
            Text(
                text = "Casi terminamos",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(20.dp))

            // Subtítulo
            Text(
                text = "Elige un nombre de usuario único para identificarte en Cycles.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(30.dp))

            // Etiqueta del campo
            Text(
                text = "Nombre de usuario",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))

            // Campo de Texto
            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                shape = RoundedCornerShape(6.dp),
                placeholder = { Text("Ej: martin_dev", style = MaterialTheme.typography.bodySmall) },
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = "Icono de usuario")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .height(53.dp)
                    .fillMaxWidth(),
                enabled = !isLoading // Se bloquea mientras carga
            )
            Spacer(Modifier.height(12.dp))

            if (showAgeInput) {
                Spacer(Modifier.height(16.dp))
                Text("Fecha de nacimiento", style = MaterialTheme.typography.labelLarge)

                DateOfBirthPicker(
                    selectedDate = dob,
                    onDateSelected = { newDateString ->
                        focusManager.clearFocus()
                        viewModel.updateDateOfBirth(newDateString)
                    },
                    modifier = Modifier.fillMaxWidth(),

                    )
            }

            // Mensaje de Error (solo si hay error)
            if (!errorMsg.isNullOrEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = errorMsg!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Empujamos el botón hacia abajo (opcional, si prefieres pegado al input quita el weight)
            Spacer(modifier = Modifier.weight(1f))

            // Botón Finalizar Registro
            Button(
                onClick = {
                    focusManager.clearFocus()
                    // Aquí llamamos a la función que dispara todo el proceso final
                    viewModel.checkUsernameAndRegister(navController)
                },
                shape = RoundedCornerShape(25),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                // Solo habilitado si no está cargando y el usuario escribió al menos 4 caracteres
                enabled = !isLoading && name.length >= 4
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Finalizar Registro")
                }
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}