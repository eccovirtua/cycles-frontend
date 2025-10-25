package com.example.cycles.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cycles.data.SearchResultItem
import com.example.cycles.data.ItemType
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.ListDetailEvent
import com.example.cycles.viewmodel.ListDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailScreen(
    navController: NavController,
    viewModel: ListDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Escucha eventos de una sola vez (como "Borrado Exitoso")
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is ListDetailEvent.ListDeleted -> {
                    // Volvemos a la pantalla anterior
                    navController.popBackStack()
                }
            }
        }
    }

    // --- Diálogo de Edición ---
    if (showEditDialog && state.listDetails != null) {
        ListEditDialog(
            viewModel = viewModel,
            listDetails = state.listDetails!!,
            onDismiss = { showEditDialog = false }
        )
    }

    // --- Diálogo de Borrado ---
    if (showDeleteDialog) {
        DeleteConfirmDialog(
            listName = state.listDetails?.name ?: "esta lista",
            onConfirm = {
                showDeleteDialog = false
                viewModel.deleteList()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.listDetails?.name ?: "Cargando...", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    // Botón de Añadir Item (navega a Búsqueda)
                    IconButton(onClick = { navController.navigate(Screen.Search.route) }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir item")
                    }
                    // Botón de Editar Lista
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar lista")
                    }
                    // Botón de Borrar Lista
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar lista", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            val currentListDetails = state.listDetails


            when {
                state.isLoading && currentListDetails == null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.error != null -> {
                    Text(
                        text = "Error: ${state.error}",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                currentListDetails == null -> {
                    Text(
                        text = "Lista no encontrada",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                currentListDetails.items.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Esta lista está vacía")
                        Button(onClick = { navController.navigate(Screen.Search.route) }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Añadir items")
                        }
                    }
                }
                else -> {
                    // Lista de items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(currentListDetails.items, key = { it.itemId }) { item ->
                            ItemInListRow(
                                item = item,
                                onRemoveClick = {
                                    viewModel.removeItem(item.itemId)
                                },
                                onItemClick = {
                                    // Navegamos al detalle del item
                                    val itemType = try { ItemType.valueOf(item.domain.uppercase()) } catch (_: Exception) { ItemType.BOOK }
                                    navController.navigate(Screen.ItemDetail.createRoute(item.itemId, itemType))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


// --- Componente: Fila de Item en la Lista ---
@Composable
fun ItemInListRow(
    item: SearchResultItem,
    onRemoveClick: () -> Unit,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onItemClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Imagen
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            // Texto
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(
                    "Tipo: ${item.domain.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Botón de Quitar
            IconButton(onClick = onRemoveClick) {
                Icon(
                    Icons.Default.RemoveCircleOutline,
                    contentDescription = "Quitar item",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// --- Componente: Diálogo de Edición ---
@Composable
fun ListEditDialog(
    viewModel: ListDetailViewModel,
    listDetails: com.example.cycles.data.UserListDetail,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(listDetails.name) }
    var icon by remember { mutableStateOf(listDetails.iconName ?: availableIcons.keys.first()) }
    var color by remember { mutableStateOf(listDetails.colorHex ?: availableColors.first()) } // <-- Añadir también por seguridad

    val state by viewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Lista") },
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
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.updateList(name, icon, color) {
                        isLoading = false
                        onDismiss() // Cierra el diálogo si tiene éxito
                    }
                },
                enabled = name.isNotBlank() && !isLoading && state.error == null
            ) {
                if(isLoading) CircularProgressIndicator(Modifier.size(20.dp))
                else Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

// --- Componente: Diálogo de Confirmación de Borrado ---
@Composable
fun DeleteConfirmDialog(
    listName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Borrar Lista") },
        text = { Text("¿Estás seguro de que quieres borrar la lista \"$listName\"? Esta acción no se puede deshacer.") },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Borrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}