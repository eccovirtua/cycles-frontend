package com.example.cycles.ui.components
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.LocalPizza
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Games
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.cycles.viewmodel.ListsViewModel

// 1. DEFINICIÓN DE ICONOS Y COLORES (Soluciona 'Unresolved reference')

val defaultIcon: ImageVector = Icons.Default.Favorite

val availableIcons: Map<String, ImageVector> = mapOf(
    "favorite" to Icons.Default.Favorite,
    "star" to Icons.Default.Star,
    "movie" to Icons.Default.Movie,
    "book" to Icons.Default.Book,
    "music" to Icons.Default.MusicNote,
    "person" to Icons.Default.Person,
    "food" to Icons.Default.LocalPizza,
    "sport" to Icons.Default.SportsSoccer,
    "game" to Icons.Default.Games
)

val availableColors = listOf(
    "#F44336", "#E91E63", "#9C27B0", "#673AB7", "#3F51B5",
    "#2196F3", "#00BCD4", "#4CAF50", "#FFC107", "#FF5722",
    "#795548", "#607D8B", "#000000"
)

// 2. DIÁLOGO DE CREACIÓN (Soluciona 'ListCreateDialog')

@Composable
fun ListCreateDialog(
    viewModel: ListsViewModel,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    // Seleccionamos el primer icono y color por defecto
    var iconKey by remember { mutableStateOf(availableIcons.keys.first()) }
    var colorHex by remember { mutableStateOf(availableColors.first()) }

    val state by viewModel.uiState.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Lista", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    placeholder = { Text("Ej. Favoritos 2024") },
                    isError = state.error != null,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Divider()

                Text("Icono", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                IconPicker(selectedIconName = iconKey, onIconSelected = { iconKey = it })

                Text("Color de portada", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
                ColorPicker(selectedColorHex = colorHex, onColorSelected = { colorHex = it })

                if (state.error != null) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isLoading = true
                    viewModel.createList(name, iconKey, colorHex) {
                        isLoading = false
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank() && !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if(isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text("Crear Lista")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("Cancelar")
            }
        }
    )
}

// 3. SELECTORES (Helpers para el diálogo)

@Composable
fun IconPicker(selectedIconName: String, onIconSelected: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(availableIcons.entries.toList()) { (name, iconVector) ->
            val isSelected = name == selectedIconName

            // Item visual del icono
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(
                        width = 2.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onIconSelected(name) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ColorPicker(selectedColorHex: String, onColorSelected: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
    ) {
        items(availableColors) { colorHex ->
            val color = try { Color(colorHex.toColorInt()) } catch (e: Exception) { Color.Gray }
            val isSelected = colorHex == selectedColorHex

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(colorHex) }
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}