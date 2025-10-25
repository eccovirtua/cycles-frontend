package com.example.cycles.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cycles.data.UserListBasic
import com.example.cycles.viewmodel.ListsViewModel
import androidx.core.graphics.toColorInt
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle

// --- Diccionarios de Iconos y Colores (Solo para la UI) ---

val availableIcons = mapOf(
    "favorite" to Icons.Default.Favorite,
    "star" to Icons.Default.Star,
    "movie" to Icons.Default.Movie,
    "book" to Icons.Default.Book,
    "music" to Icons.Default.MusicNote
)

val defaultIcon = Icons.Default.Favorite

val availableColors = listOf(
    "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
    "#2196F3", "#00BCD4", "#4CAF50", "#FFC107", "#FF5722"
)

// --- Pantalla Principal ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    navController: NavController,
    viewModel: ListsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Recargar listas cada vez que esta pantalla aparece
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            // Ponemos la llamada a loadLists() aquí dentro
            viewModel.loadLists()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Listas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.clearError() // Limpiamos errores antiguos
                showCreateDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Crear Lista")
            }
        }
    ) { padding ->

        if (showCreateDialog) {
            ListCreateDialog(
                viewModel = viewModel,
                onDismiss = { showCreateDialog = false }
            )
        }

        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading && state.lists.isEmpty() -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                state.lists.isEmpty() -> {
                    Text(
                        text = "Aún no tienes listas. ¡Crea una con el botón +!",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.lists, key = { it.listId }) { list ->
                            ListRowItem(
                                list = list,
                                onClick = {
                                    // Navegamos a la pantalla de detalle
                                    navController.navigate("list_detail/${list.listId}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Componentes de la UI ---

@Composable
fun ListRowItem(list: UserListBasic, onClick: () -> Unit) {
    val icon = availableIcons[list.iconName] ?: defaultIcon
    val color = try { Color(list.colorHex.toColorInt()) } catch (_: Exception) { Color.Gray }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Icono
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = list.name,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }
            // 2. Texto
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = list.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${list.itemCount} ${if (list.itemCount == 1) "item" else "items"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ListCreateDialog(
    viewModel: ListsViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf(availableIcons.keys.first()) }
    var color by remember { mutableStateOf(availableColors.first()) }

    val state by viewModel.uiState.collectAsState() // Usamos el state del ListsViewModel
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Crear Nueva Lista") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la lista") },
                    isError = state.error != null,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Elegir Icono", style = MaterialTheme.typography.labelMedium)
                IconPicker(selectedIconName = icon, onIconSelected = { icon = it })

                Text("Elegir Color", style = MaterialTheme.typography.labelMedium)
                ColorPicker(selectedColorHex = color, onColorSelected = { color = it })

                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.createList(name, icon, color) {
                        isLoading = false
                        onDismiss() // Cierra el diálogo si tiene éxito
                    }
                    // Si hay un error, isLoading se manejará por el state.error
                },
                enabled = name.isNotBlank() && !isLoading && state.error == null
            ) {
                if(isLoading) {
                    CircularProgressIndicator(Modifier.size(20.dp))
                } else {
                    Text("Crear")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun IconPicker(
    selectedIconName: String,
    onIconSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(availableIcons.entries.toList()) { (name, icon) ->
            IconPickerItem(
                icon = icon,
                isSelected = name == selectedIconName,
                onClick = { onIconSelected(name) }
            )
        }
    }
}

@Composable
fun IconPickerItem(icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick)
            .border(
                width = 2.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ColorPicker(
    selectedColorHex: String,
    onColorSelected: (String) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(availableColors) { colorHex ->
            val color = Color(colorHex.toColorInt())
            ColorPickerItem(
                color = color,
                isSelected = colorHex == selectedColorHex,
                onClick = { onColorSelected(colorHex) }
            )
        }
    }
}

@Composable
fun ColorPickerItem(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable(onClick = onClick)
            .border(
                width = 3.dp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f) else Color.Transparent,
                shape = CircleShape
            )
    )
}