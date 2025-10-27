package com.example.cycles.ui.screens



import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.cycles.data.ItemType
import com.example.cycles.data.SearchResultItem
import com.example.cycles.data.UserListBasic
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.UserProfileState
import com.example.cycles.viewmodel.UserProfileViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import coil.compose.AsyncImage
import androidx.core.graphics.toColorInt

// --- Constantes (Nivel de Archivo) ---
val profileSections = listOf("Listas", "Archivadas", "Favoritos")



// --- Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class) // Añadir ExperimentalFoundationApi
@Composable
fun UserProfileScreen(
    navController: NavController,
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    screenPadding: PaddingValues,
    onLogoutClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    // Carga inicial y recarga de sección
    LaunchedEffect(Unit) {
        viewModel.loadUserProfileData()
        // Carga la sección inicial si aún no se ha cargado
        if (state.activeLists.isEmpty() && state.archivedLists.isEmpty() && state.favoriteItems.isEmpty()) {
            viewModel.onSectionSelected(state.sectionIndex) // Carga la pestaña actual
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.username.ifEmpty { "@UsuarioCycles" }, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding( // Padding exterior (ignora top/bottom del Scaffold)
                    start = screenPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = screenPadding.calculateEndPadding(LocalLayoutDirection.current)
                ),
            contentPadding = PaddingValues( // Padding interior (respeta top/bottom del Scaffold)
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding() + 150.dp,
                start = 0.dp,
                end = 0.dp
            )
        ) {
            // --- Header Item ---
            item {
                ProfileHeaderContent(state, onEditClick)
            }

            // --- Tabs Header ---
            stickyHeader {
                SectionTabs(state.sectionIndex, viewModel::onSectionSelected)
            }

            // --- Section Content Item ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .heightIn(min = 300.dp) // Altura mínima para contenido
                        .padding(top = 8.dp) // Espacio después de las pestañas
                ) {
                    // Contenido de la sección seleccionada
                    SectionContent(
                        state = state,
                        navController = navController,
                        onArchive = viewModel::archiveList,
                        onUnarchive = viewModel::unarchiveList
                    )

                    // Indicador de carga centrado
                    if (state.isLoadingSection) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }

                    // Mensaje de error centrado
                    if (!state.isLoadingSection && state.error != null && state.sectionIndex in 0..2){
                        Text(
                            text = state.error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center).padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// --- Composables Auxiliares (Nivel de Archivo) ---

@Composable
fun ProfileHeaderContent(state: UserProfileState, onEditClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Imagen de Portada
        Image(
            painter = rememberAsyncImagePainter(model = state.coverImageUrl),
            contentDescription = "Portada de perfil",
            modifier = Modifier
                .fillMaxWidth()
                .height(177.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant), // Color de fondo placeholder
            contentScale = ContentScale.Crop
        )
        // Contenido debajo de la portada
        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            // Fila: Foto de perfil y botón Editar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp), // Superponer foto sobre portada
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween // Empuja el botón a la derecha
            ) {
                // Foto de Perfil
                Image(
                    painter = rememberAsyncImagePainter(model = state.profileImageUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp) // Reducir tamaño
                        .background(MaterialTheme.colorScheme.surface) // Fondo para borde
                        .border(2.dp, MaterialTheme.colorScheme.surface), // Borde
                    contentScale = ContentScale.Crop
                )
                // Botón Editar Perfil
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.padding(bottom = 38.dp) // Ajustar altura de boton editar perfil
                ) {
                    Text("Editar Perfil")
                }
            }
            // Columna: Nombre, Username, Bio, Stats
            Column(modifier = Modifier.offset(y = (-40).dp)) { // Ajustar offset
                Text(
                    text = state.name.ifEmpty { "Nombre de Usuario" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.username.ifEmpty { "@usuario" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.bio.ifEmpty { "Sin biografía." },
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3, // Limitar líneas
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Fila Seguidores/Seguidos (Placeholders)

            }
        }
    }
}

@Composable
fun SectionTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface), // Fondo para visibilidad
        containerColor = MaterialTheme.colorScheme.surface // Color explícito
    ) {
        profileSections.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = { Text(title) }
            )
        }
    }
}

@Composable
fun SectionContent(
    state: UserProfileState,
    navController: NavController,
    onArchive: (String) -> Unit,
    onUnarchive: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Mensaje mientras carga (si no hay datos previos)
        if (state.isLoadingSection &&
            (state.sectionIndex == 0 && state.activeLists.isEmpty() ||
                    state.sectionIndex == 1 && state.archivedLists.isEmpty() ||
                    state.sectionIndex == 2 && state.favoriteItems.isEmpty())
        ) {
            // No mostrar nada o un placeholder sutil, el indicador global ya está
        } else {
            // Contenido basado en la pestaña seleccionada
            when (state.sectionIndex) {
                0 -> { // Listas Activas
                    if (!state.isLoadingSection && state.activeLists.isEmpty()) {
                        Text("No tienes listas activas.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
                    } else {
                        state.activeLists.forEach { list ->
                            ListRowItemWithAction(
                                list = list,
                                onClick = { navController.navigate("list_detail/${list.listId}") },
                                actionIcon = Icons.Default.Archive,
                                actionContentDescription = "Archivar",
                                onActionClick = { onArchive(list.listId) }
                            )
                        }
                    }
                }
                1 -> { // Listas Archivadas
                    if (!state.isLoadingSection && state.archivedLists.isEmpty()) {
                        Text("No tienes listas archivadas.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
                    } else {
                        state.archivedLists.forEach { list ->
                            ListRowItemWithAction(
                                list = list,
                                onClick = { navController.navigate("list_detail/${list.listId}") },
                                actionIcon = Icons.Default.Unarchive,
                                actionContentDescription = "Desarchivar",
                                onActionClick = { onUnarchive(list.listId) }
                            )
                        }
                    }
                }
                2 -> { // Favoritos
                    if (!state.isLoadingSection && state.favoriteItems.isEmpty()) {
                        Text("No tienes items favoritos.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 16.dp))
                    } else {
                        state.favoriteItems.forEach { item ->
                            FavoriteItemRow(
                                item = item,
                                onClick = {
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

@SuppressLint("UseKtx")
@Composable
fun ListRowItemWithAction(
    list: UserListBasic,
    onClick: () -> Unit,
    actionIcon: ImageVector,
    actionContentDescription: String,
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Sombra sutil
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), // Ajustar padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = availableIcons[list.iconName] ?: defaultIcon
            val color = try { Color(list.colorHex.toColorInt()) } catch (_: Exception) { Color.Gray }
            // Icono de la lista
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon, // Explicitly name the parameter
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            // Nombre y contador
            Column(modifier = Modifier.weight(1f)) {
                Text(list.name, style=MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${list.itemCount} items", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            // Botón de Acción
            IconButton(onClick = onActionClick) {
                Icon(actionIcon, contentDescription = actionContentDescription, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun FavoriteItemRow(
    item: SearchResultItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // Sombra sutil
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp) // Más espacio
        ) {
            // Imagen
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(50.dp) // Ancho fijo
                    .height(75.dp) // Alto fijo (ratio ~1:1.5)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            // Título y Tipo
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis) // Permitir 2 líneas
                Spacer(Modifier.height(4.dp))
                Text(
                    "Tipo: ${item.domain.replaceFirstChar { it.uppercase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Podrías añadir un IconButton aquí si necesitas quitar favoritos desde el perfil
        }
    }
}