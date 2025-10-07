package com.example.cycles.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.cycles.viewmodel.ChooseUsernameViewModel


@Composable
fun ChooseUsernameScreen(
    navController: NavController,
    token: String,
    paddingValues: PaddingValues,
    viewModel: ChooseUsernameViewModel = hiltViewModel()
) {
    val name by viewModel.name.collectAsState()
    val isAvailable by viewModel.isAvailable.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 1.dp),
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
            Spacer(Modifier.height(20.dp)) //separacion de titulo y subtitulo
            Text(
                text = "Elige un nombre de usuario para tu cuenta y perfil y verifica que esté disponible",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))


            OutlinedTextField(
                value = name,
                onValueChange = viewModel::onNameChange,
                shape = RoundedCornerShape(6.dp),
                leadingIcon = {
                    Icon(Icons.Filled.AlternateEmail, contentDescription = "Icono de correo")
                },
                placeholder = {
                    Text(
                        "Ingresa un nombre de usuario...",
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                 modifier = Modifier.height(53.dp).fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick =
                    { viewModel.checkAvailability() },
                enabled = name.isNotBlank() && !isLoading,
                shape = RoundedCornerShape(25.dp),
                modifier = Modifier.fillMaxWidth()
            )
            {
                Text("Verificar disponibilidad")
            }
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error)
            if (isAvailable == true) {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.saveUsername(navController) },
                    modifier = Modifier.fillMaxWidth().size(20.dp),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text("Continuar")
                }
            }
        }
    }
}
