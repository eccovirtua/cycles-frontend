package com.example.cycles.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cycles.viewmodel.EditProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: EditProfileViewModel = hiltViewModel() // Inyección Hilt
) {
    val state by viewModel.state.collectAsState()


    var (showDiscardDialog, setShowDiscardDialog) = remember { mutableStateOf(false) }
    val (showSaveConfirmationDialog, setShowSaveConfirmationDialog) = remember { mutableStateOf(false) }

    // Lógica para decidir qué hacer cuando se intenta volver atrás
    val onBackRequested = {
        if (viewModel.hasChanges()) {
            setShowDiscardDialog(true)
        } else {
            onBackClick()
        }
    }

    BackHandler {
        onBackRequested()
    }

    LaunchedEffect(state.isSavedSuccess) {
        if (state.isSavedSuccess) {
            onBackClick()
        }
    }

    // Launchers de Galería
    val profilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.onNewProfileImageSelected(uri) }

    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri -> viewModel.onNewCoverImageSelected(uri) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackRequested) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancelar")
                    }
                },
                actions = {
                    Button(
                        onClick = { setShowSaveConfirmationDialog(true) },
                        enabled = !state.isLoading,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Text("Guardar")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // HEADER: Le pasamos la lógica visual
            // Prioridad visual: 1. Nueva URI local (preview), 2. URL remota actual
            EditProfileHeader(
                profileUri = state.newProfileUri,
                profileUrl = state.currentProfileUrl,
                coverUri = state.newCoverUri,
                coverUrl = state.currentCoverUrl,
                onEditProfileClick = {
                    profilePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
                onEditCoverClick = {
                    coverPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                Text("Información Pública", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.country,
                    onValueChange = { viewModel.onCountryChange(it) },
                    label = { Text("País") },
                    leadingIcon = { Icon(Icons.Default.Public, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))

                Text("Privacidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

                ListItem(
                    headlineContent = { Text("Mostrar edad en el perfil") },
                    supportingContent = { Text("Si desactivas esto, ocultas la edad de tu perfil") },
                    leadingContent = {
                        Icon(if (state.isAgeVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null)
                    },
                    trailingContent = {
                        Switch(
                            checked = state.isAgeVisible,
                            onCheckedChange = { viewModel.onAgeVisibilityChange(it) }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onAgeVisibilityChange(!state.isAgeVisible) }
                )
            }
        }
    }
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("¿Descartar cambios?") },
            text = { Text("Has realizado modificaciones. Si sales ahora, se perderán los cambios.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onBackClick()
                    }
                ) {
                    Text("Descartar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text("Seguir editando")
                }
            }
        )
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { setShowDiscardDialog(false) },
            title = { Text("¿Descartar cambios?") },
            text = { Text("Has realizado modificaciones. Si sales ahora, se perderán los cambios.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        setShowDiscardDialog(false)
                        onBackClick()
                    }
                ) {
                    Text("Descartar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowDiscardDialog(false) }) {
                    Text("Seguir editando")
                }
            }
        )
    }

    if (showSaveConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { setShowSaveConfirmationDialog(false) },
            title = { Text("Guardar cambios") },
            text = { Text("¿Quieres actualizar tu perfil con esta información?") },
            confirmButton = {
                Button(
                    onClick = {
                        setShowSaveConfirmationDialog(false)
                        viewModel.saveChanges()
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowSaveConfirmationDialog(false) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


// --- Header Actualizado para soportar URL y URI ---
@Composable
fun EditProfileHeader(
    profileUri: Uri?, // Foto nueva local
    profileUrl: String?, // Foto vieja remota
    coverUri: Uri?,
    coverUrl: String?,
    onEditProfileClick: () -> Unit,
    onEditCoverClick: () -> Unit
) {
    // Lógica de visualización: Si hay URI nueva, usa esa. Si no, usa URL remota. Si no, placeholder.
    val finalProfileModel = profileUri ?: profileUrl ?: "https://via.placeholder.com/150"
    val finalCoverModel = coverUri ?: coverUrl ?: "https://via.placeholder.com/600x200"

    Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
        // Portada
        Box(
            modifier = Modifier.fillMaxWidth().height(180.dp).background(Color.DarkGray).clickable(onClick = onEditCoverClick),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(finalCoverModel).crossfade(true).build(),
                contentDescription = "Portada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White)
                    Text("Editar portada", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Foto Perfil
        Box(modifier = Modifier.align(Alignment.BottomStart).padding(start = 24.dp)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(120.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface).border(4.dp, MaterialTheme.colorScheme.surface, CircleShape).clickable(onClick = onEditProfileClick)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(finalProfileModel).crossfade(true).build(),
                    contentDescription = "Foto perfil",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }
        }
    }
}