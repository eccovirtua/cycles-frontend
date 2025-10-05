package com.example.cycles.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.cycles.ui.theme.AnimatedBackground
import com.example.cycles.viewmodel.ChooseUsernameViewModel


@Composable
fun ChooseUsernameScreen(
    navController: NavController,
    token: String,
    viewModel: ChooseUsernameViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val isAvailable by viewModel.isAvailable.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    AnimatedBackground(Modifier.fillMaxSize().padding(32.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = viewModel::onNameChange,
            label = { Text("Elige un nombre de usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick =
            { viewModel.checkAvailability() },
            enabled = name.isNotBlank() && !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Verificar disponibilidad")
        }
        if (isLoading) CircularProgressIndicator()
        if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
        if (isAvailable == true) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = { viewModel.saveUsername(navController) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continuar")
            }
        }
    }
}
