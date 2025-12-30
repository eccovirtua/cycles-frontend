package com.example.cycles.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cycles.data.ItemType
import com.example.cycles.ui.components.SearchFilter
import com.example.cycles.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onItemClick: (itemId: String, itemType: ItemType) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedFilter by remember { mutableStateOf(SearchFilter.ALL) }
    var showDialogForItemId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // 1. BARRA DE BÚSQUEDA
            SearchBarWithFilter(
                query = uiState.query,
                onQueryChange = viewModel::updateQuery,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. CONTENIDO
            if (uiState.query.isEmpty()) {
                DiscoveryFeed()
            } else {
                SearchResultsList(
                    uiState = uiState,
                    onItemClick = onItemClick,
                    onAddToListClick = { showDialogForItemId = it }
                )
            }
        }
    }
}

// --- COMPONENTE 1: BARRA DE BÚSQUEDA (Sin cambios mayores) ---
@Composable
fun SearchBarWithFilter(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedFilter: SearchFilter,
    onFilterSelected: (SearchFilter) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Buscar en ${selectedFilter.label}...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        trailingIcon = {
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        Icons.Default.Tune,
                        contentDescription = "Filtros",
                        tint = if(selectedFilter != SearchFilter.ALL) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    SearchFilter.entries.forEach { filter ->
                        DropdownMenuItem(
                            text = { Text(filter.label) },
                            leadingIcon = { Icon(filter.icon, null) },
                            onClick = {
                                onFilterSelected(filter)
                                expanded = false
                            },
                            trailingIcon = {
                                if (filter == selectedFilter) Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                            }
                        )
                    }
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )
}

// --- COMPONENTE 2: FEED DE DESCUBRIMIENTO (Tu código original intacto) ---
@Composable
fun DiscoveryFeed() {
    // Datos falsos para decorar
    val randomLists = listOf("Terror 80s", "Cyberpunk Vibes", "Gym Motivation", "Chill Sunday")
    val recentReviews = listOf(
        Pair("Inception", 5.0), Pair("Dune Part 2", 4.5), Pair("Metallica - Black", 5.0)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // SECCIÓN 1: Listas Recomendadas
        item {
            Text(
                "Listas populares hoy",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DiscoveryListCard(randomLists[0], Color(0xFFEF5350), Modifier.weight(1f))
                    DiscoveryListCard(randomLists[1], Color(0xFF42A5F5), Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DiscoveryListCard(randomLists[2], Color(0xFF66BB6A), Modifier.weight(1f))
                    DiscoveryListCard(randomLists[3], Color(0xFFFFA726), Modifier.weight(1f))
                }
            }
        }

        // SECCIÓN 2: Reseñas Recientes
        item {
            Text(
                "Reseñas de la comunidad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(recentReviews) { (title, rating) ->
                    DiscoveryReviewCard(title, rating)
                }
                items(3) {
                    DiscoveryReviewCard("Item #$it", 4.0)
                }
            }
        }

        // SECCIÓN 3: Categorías Rápidas
        item {
            Text("Explorar por categoría", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                CategoryShortcut(Icons.Default.Movie, "Cine")
                CategoryShortcut(Icons.Default.Book, "Libros")
                CategoryShortcut(Icons.Default.MusicNote, "Música")
                CategoryShortcut(Icons.Default.Person, "Usuarios")
            }
        }
    }
}

// --- SUB-COMPONENTES VISUALES PARA DISCOVERY (RESTAURADOS) ---

@Composable
fun DiscoveryListCard(title: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.8f))
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.BottomStart) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Icon(
                Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.TopEnd)
            )
        }
    }
}

@Composable
fun DiscoveryReviewCard(title: String, rating: Double) {
    Card(
        modifier = Modifier.width(160.dp).height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(24.dp), shape = CircleShape, color = Color.Gray) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text("Usuario", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                Text("$rating", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

@Composable
fun CategoryShortcut(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(55.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}


// --- COMPONENTE 3: RESULTADOS DE BÚSQUEDA (ACTUALIZADO) ---
@Composable
fun SearchResultsList(
    uiState: com.example.cycles.viewmodel.SearchUiState,
    onItemClick: (String, ItemType) -> Unit,
    onAddToListClick: (String) -> Unit
) {
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.error, color = MaterialTheme.colorScheme.error)
        }
        return
    }

    if (uiState.results.isEmpty() && uiState.query.isNotEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No encontramos nada para \"${uiState.query}\"", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp), // Un poco más de espacio
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Text(
                "Resultados (${uiState.results.size})",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        items(uiState.results, key = { it.itemId }) { item ->
            SearchResultItemCard(
                item = item,
                onItemClick = {
                    // Conversión segura de String a Enum
                    val typeEnum = try {
                        ItemType.valueOf(item.type.uppercase())
                    } catch (e: Exception) {
                        ItemType.MOVIE // Default seguro
                    }
                    onItemClick(item.itemId, typeEnum)
                },
                onAddToListClick = { onAddToListClick(item.itemId) }
            )
        }
    }
}

// --- EL NUEVO CARD CON COIL ---
@Composable
fun SearchResultItemCard(
    item: com.example.cycles.data.SearchResultItem,
    onItemClick: () -> Unit,
    onAddToListClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Altura fija para que se vea ordenado
            .clickable(onClick = onItemClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. IMAGEN (PÓSTER)
            if (item.imageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop, // Importante para que llene el espacio
                    modifier = Modifier
                        .width(54.dp) // Proporción de póster aprox
                        .fillMaxHeight()
                )
            } else {
                // Fallback si no hay imagen (Icono)
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (item.type.uppercase()) {
                            "MOVIE" -> Icons.Default.Movie
                            "BOOK" -> Icons.Default.Book
                            else -> Icons.Default.Search
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. TEXTOS
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Subtítulo (Año) y Tipo
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.subtitle, // Ej: "2023"
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Chip pequeño para el tipo
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.type.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // 3. BOTÓN AÑADIR
            IconButton(onClick = onAddToListClick) {
                Icon(
                    Icons.AutoMirrored.Filled.PlaylistAdd,
                    contentDescription = "Añadir a lista",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}