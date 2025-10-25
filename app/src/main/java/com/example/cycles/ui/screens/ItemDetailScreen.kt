package com.example.cycles.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.data.ItemType
import com.example.cycles.ui.components.AddToListDialog
import com.example.cycles.viewmodel.ItemDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    itemId: String,
    itemType: ItemType, // Mantenido por si se usa en el futuro
    viewModel: ItemDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val item = uiState.currentItemDetail
    val isLoading = uiState.isDetailLoading
    val error = uiState.detailError


    var showAddToListDialog by remember { mutableStateOf(false) }
    if (showAddToListDialog) {
        AddToListDialog(
            itemIdToAdd = itemId,
            onDismiss = { showAddToListDialog = false }
        )
    }

    // Cargar detalles al componer la pantalla por primera vez
    LaunchedEffect(itemId) {
        viewModel.loadItemDetails(itemId)
    }

    // Limpiar detalles al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearItemDetails()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.title ?: "Cargando...") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddToListDialog = true }) {
                        Icon(Icons.AutoMirrored.Filled.PlaylistAdd, contentDescription = "Añadir a lista")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: $error", color = MaterialTheme.colorScheme.error)
                    }
                }
                item != null -> {
                    ItemDetailContent(item = item)
                }
            }
        }
    }
}

@Composable
fun ItemDetailContent(item: ItemDetailResponse) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.CenterHorizontally),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            item.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        SectionTitle("Resumen")
        // TODO: Asegúrate de que el backend envíe un campo 'description' o 'summary'
        Text(item.title, style = MaterialTheme.typography.bodyLarge)

        SectionTitle("Detalles")

        item.year?.let { DetailRow("Año", it) }
        item.genres?.let { DetailRow("Géneros", it.joinToString(", ")) }
        item.artist?.let { DetailRow("Artista", it) }
        item.googleAvgRating?.let { DetailRow("Rating (Google)", "%.1f / 5".format(it)) }
        item.imdbScore?.let { DetailRow("Rating (IMDb)", "%.1f / 10".format(it)) }
        item.listeners?.let { DetailRow("Oyentes", it.toString()) }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            thickness = DividerDefaults.Thickness,
            color = DividerDefaults.color
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            "$label:",
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )
        Text(value)
    }
}
