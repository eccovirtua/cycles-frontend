//package com.example.cycles.ui.components
//
//import androidx.compose.animation.AnimatedContent
//import androidx.compose.foundation.BorderStroke
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.CheckCircle
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextOverflow
//import androidx.compose.ui.unit.dp
//import androidx.core.graphics.toColorInt
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.cycles.data.UserListBasic
//import com.example.cycles.ui.screens.ListCreateDialog
//import com.example.cycles.ui.screens.availableIcons
//import com.example.cycles.ui.screens.defaultIcon
//import com.example.cycles.viewmodel.AddToListViewModel
//import com.example.cycles.viewmodel.ListsViewModel
//import kotlinx.coroutines.delay
//
//@Composable
//fun AddToListDialog(
//    itemIdToAdd: String,
//    onDismiss: () -> Unit,
//    addToListViewModel: AddToListViewModel = hiltViewModel(),
//    listsViewModel: ListsViewModel = hiltViewModel()
//) {
//    val state by addToListViewModel.uiState.collectAsState()
//    var showCreateListDialog by remember { mutableStateOf(false) }
//
//    // Efecto para cerrar automáticamente si hay éxito
//    LaunchedEffect(state.successMessage) {
//        if (state.successMessage != null) {
//            delay(1500) // Esperar 1.5 seg para que el usuario lea el mensaje
//            onDismiss()
//        }
//    }
//
//    // --- Diálogo anidado para CREAR lista ---
//    if (showCreateListDialog) {
//        ListCreateDialog(
//            viewModel = listsViewModel,
//            onDismiss = {
//                showCreateListDialog = false
//                // Recargar listas tras crear una nueva
//                addToListViewModel.loadLists()
//            }
//        )
//    }
//
//    // --- Diálogo Principal ---
//    AlertDialog(
//        onDismissRequest = {
//            addToListViewModel.clearError()
//            onDismiss()
//        },
//        containerColor = MaterialTheme.colorScheme.surface,
//        title = {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Icon(
//                    Icons.AutoMirrored.Filled.PlaylistAdd,
//                    contentDescription = null,
//                    tint = MaterialTheme.colorScheme.primary
//                )
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Guardar en...", fontWeight = FontWeight.Bold)
//            }
//        },
//        text = {
//            // Usamos AnimatedContent para transiciones suaves entre estados (Carga -> Lista -> Éxito)
//            AnimatedContent(
//                targetState = state,
//                label = "dialog_content"
//            ) { targetState ->
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .heightIn(min = 150.dp, max = 400.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    when {
//                        // 1. ESTADO DE CARGA
//                        targetState.isLoading -> {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                CircularProgressIndicator()
//                                Spacer(modifier = Modifier.height(16.dp))
//                                Text("Cargando tus listas...", style = MaterialTheme.typography.bodySmall)
//                            }
//                        }
//
//                        // 2. ESTADO DE ÉXITO
//                        targetState.successMessage != null -> {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Icon(
//                                    Icons.Default.CheckCircle,
//                                    contentDescription = null,
//                                    tint = Color(0xFF4CAF50), // Verde éxito
//                                    modifier = Modifier.size(48.dp)
//                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    targetState.successMessage,
//                                    fontWeight = FontWeight.Bold,
//                                    color = MaterialTheme.colorScheme.onSurface
//                                )
//                            }
//                        }
//
//                        // 3. ESTADO DE ERROR
//                        targetState.error != null -> {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Icon(
//                                    Icons.Default.Close,
//                                    contentDescription = null,
//                                    tint = MaterialTheme.colorScheme.error,
//                                    modifier = Modifier.size(48.dp)
//                                )
//                                Spacer(modifier = Modifier.height(8.dp))
//                                Text(
//                                    targetState.error,
//                                    color = MaterialTheme.colorScheme.error,
//                                    style = MaterialTheme.typography.bodyMedium
//                                )
//                            }
//                        }
//
//                        // 4. ESTADO VACÍO (Sin listas)
//                        targetState.lists.isEmpty() -> {
//                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                                Text("No tienes listas creadas.", color = MaterialTheme.colorScheme.onSurfaceVariant)
//                                Spacer(modifier = Modifier.height(16.dp))
//                                Button(onClick = { showCreateListDialog = true }) {
//                                    Text("Crear mi primera lista")
//                                }
//                            }
//                        }
//
//                        // 5. LISTA DE SELECCIÓN (Estado Normal)
//                        else -> {
//                            LazyColumn(
//                                modifier = Modifier.fillMaxSize(),
//                                verticalArrangement = Arrangement.spacedBy(8.dp)
//                            ) {
//                                // Opción rápida para crear nueva lista al principio
//                                item {
//                                    CreateNewListShortcut { showCreateListDialog = true }
//                                }
//
//                                items(targetState.lists, key = { it.listId }) { list ->
//                                    CompactListSelectionItem(
//                                        list = list,
//                                        onClick = {
//                                            addToListViewModel.addItemToList(list, itemIdToAdd) {
//                                                // El onComplete lo maneja el LaunchedEffect de arriba
//                                            }
//                                        }
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            // El botón principal solo cierra, la acción se hace al clicar la lista
//            TextButton(onClick = onDismiss) {
//                Text("Cerrar")
//            }
//        }
//    )
//}
//
//// --- COMPONENTE VISUAL: ITEM COMPACTO (Estilo RYM pero para diálogos) ---
//
//@Composable
//fun CompactListSelectionItem(list: UserListBasic, onClick: () -> Unit) {
//    val icon = availableIcons[list.iconName] ?: defaultIcon
//    val listColor = try { Color(list.colorHex.toColorInt()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
//
//    Surface(
//        onClick = onClick,
//        shape = RoundedCornerShape(8.dp),
//        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), // Fondo sutil
//        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//        Row(
//            modifier = Modifier.padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Icono Cuadrado (Miniatura de portada)
//            Surface(
//                modifier = Modifier.size(40.dp),
//                shape = RoundedCornerShape(4.dp),
//                color = listColor.copy(alpha = 0.2f),
//                border = BorderStroke(1.dp, listColor.copy(alpha = 0.5f))
//            ) {
//                Box(contentAlignment = Alignment.Center) {
//                    Icon(
//                        imageVector = icon,
//                        contentDescription = null,
//                        tint = listColor,
//                        modifier = Modifier.size(20.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // Textos
//            Column {
//                Text(
//                    text = list.name,
//                    style = MaterialTheme.typography.bodyLarge,
//                    fontWeight = FontWeight.SemiBold,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//                Text(
//                    text = "${list.itemCount} items",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant
//                )
//            }
//        }
//    }
//}
//
//// --- ACCESO DIRECTO PARA CREAR LISTA ---
//@Composable
//fun CreateNewListShortcut(onClick: () -> Unit) {
//    OutlinedButton(
//        onClick = onClick,
//        modifier = Modifier.fillMaxWidth(),
//        shape = RoundedCornerShape(8.dp),
//        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
//        contentPadding = PaddingValues(12.dp)
//    ) {
//        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
//        Spacer(modifier = Modifier.width(8.dp))
//        Text("Crear nueva lista", fontWeight = FontWeight.Bold)
//    }
//}