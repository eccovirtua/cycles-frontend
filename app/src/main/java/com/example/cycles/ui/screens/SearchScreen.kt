package com.example.cycles.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Book // Importar iconos
import androidx.compose.material.icons.filled.Movie // Importar iconos
import androidx.compose.material.icons.filled.MusicNote // Importar iconos
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
// Importa tus modelos de datos
import com.example.cycles.data.ItemType
import com.example.cycles.data.SearchResultItem
import com.example.cycles.ui.components.AddToListDialog
import com.example.cycles.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    // Callback para navegar al detalle
    onItemClick: (itemId: String, itemType: ItemType) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialogForItemId by remember { mutableStateOf<String?>(null) }
    showDialogForItemId?.let { itemId ->
        AddToListDialog(
            itemIdToAdd = itemId,
            onDismiss = { showDialogForItemId = null }
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Spacer(modifier = Modifier.height(56.dp))

        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::updateQuery, // Llama al updateQuery con debounce
            label = { Text("Buscar Libros, Canciones o Películas") },
            trailingIcon = {
                // El icono ya no necesita llamar a performSearch directamente
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Text(text = uiState.error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            uiState.results.isNotEmpty() -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Text("Resultados (${uiState.results.size})", fontWeight = FontWeight.Bold) }
                        items(uiState.results, key = { it.itemId }) { item ->
                        SearchResultItem(
                            item = item,
                            onItemClick = {
                                val itemType = try {
                                    ItemType.valueOf(item.domain.uppercase())
                                } catch (_: Exception) { ItemType.BOOK }
                                onItemClick(item.itemId, itemType)
                            },
                            onAddToListClick = {
                                showDialogForItemId = item.itemId
                            }
                        )
                    }
                }
            }
            uiState.query.length >= 3 && !uiState.isLoading -> { // Muestra si no hay resultados y query > 3
                Text("No se encontraron resultados para: \"${uiState.query}\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            uiState.query.isEmpty() -> { // Mensaje inicial
                Text("Usa el buscador para encontrar contenido.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun SearchResultItem(
    item: SearchResultItem,
    onItemClick: () -> Unit,
    onAddToListClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick) // Llama al callback directo
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono basado en el tipo real
            Icon(
                when (item.domain.uppercase()) { // Convertir a enum o usar string directamente
                    ItemType.BOOK.name -> Icons.Default.Book
                    ItemType.SONG.name -> Icons.Default.MusicNote
                    ItemType.MOVIE.name -> Icons.Default.Movie
                    else -> Icons.Default.Book // Icono por defecto
                },
                contentDescription = item.domain,
                modifier = Modifier.size(32.dp), // Un poco más grande
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) { // Permite que el texto ocupe espacio
                Text(item.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
                Text(
                    "Tipo: ${item.domain.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onAddToListClick) {
                Icon(
                    Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = "Añadir a lista",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            // Puedes añadir la imagen aquí si quieres
            // AsyncImage(model = item.imageUrl, ...)
        }
    }
}