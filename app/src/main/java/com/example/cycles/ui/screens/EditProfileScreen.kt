package com.example.cycles.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.cycles.viewmodel.UserProfileViewModel
import com.example.cycles.viewmodel.UserProfileEvent
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // 🎯 CLAVE: Launcher para seleccionar imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                // Notificar al ViewModel con la URI local seleccionada
                viewModel.onProfilePhotoSelected(uri)
            }
        }
    )

    // 🛑 ARREGLO CLAVE: Escuchar el evento de navegación directamente desde el ViewModel.
    // Se elimina el código del Snackbar para navegar inmediatamente al recibir el evento.
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is UserProfileEvent.NavigateBack -> {
                    // Navegar de vuelta inmediatamente después de que el VM confirme el éxito
                    onBackClick()
                }
            }
        }
    }


    // 🎯 Mostrar Feedback de Error (Mantener solo el error, sin Snackbar)
    LaunchedEffect(state.error) {
        // Si hay un error, puedes considerar registrarlo o manejarlo internamente.
        // Si lo necesitas, reintroduce el Snackbar aquí sin afectar la navegación de éxito.
        if (state.error != null && !state.isLoading) {
            println("Error al guardar perfil: ${state.error}")
            // Considera reintroducir un diálogo o un Text de error en la UI aquí si es crítico.
        }
    }


    Scaffold(
        // 🛑 REMOVIDO: Se elimina el snackbarHost
        // snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("  Editar Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !state.isLoading) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                },
                actions = {
                    // Botón para guardar
                    Button(
                        onClick = {
                            // 1. Llamar a la lógica de guardado
                            viewModel.saveProfileChanges()
                        },
                        // Deshabilitar si está cargando o no hay cambios (lógica opcional)
                        enabled = !state.isLoading
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Guardar")
                        }
                    }
                    Spacer(Modifier.width(18.dp))
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.width(18.dp))

            // 1. Selector de Foto de Perfil
            ProfilePhotoSelector(
                currentUrl = state.profileImageUrl,
                newUri = state.newProfileUri
            ) {
                // Iniciar la selección de imagen (solo imágenes)
                imagePickerLauncher.launch("image/*")
            }

            Spacer(Modifier.height(32.dp))

            // 2. Campo de Edición de Nombre
            OutlinedTextField(
                value = state.newName,
                onValueChange = viewModel::onNameChange,
                label = { Text("") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )

            Spacer(Modifier.height(16.dp))

            // 3. Campo de Edición de Biografía
            OutlinedTextField(
                value = state.newBio,
                onValueChange = viewModel::onBioChange,
                label = { Text("") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            )
        }
    }
}

// --- Componente de selección de foto ---
@Composable
fun ProfilePhotoSelector(currentUrl: String, newUri: Uri?, onClick: () -> Unit) {
    // Determinar qué imagen mostrar: la URI local seleccionada o la URL actual
    // Si newUri es null, Coill cargará currentUrl
    val model = newUri ?: currentUrl

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = rememberAsyncImagePainter(model = model),
            contentDescription = "Foto de perfil",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(150.dp)

                .background(Color.Gray)
                .clickable(onClick = onClick) // Hacer toda la imagen clickeable
        )
        Text(
            text = "Cambiar foto",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(top = 8.dp)
        )
    }
}
