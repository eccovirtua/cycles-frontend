package com.example.cycles.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.example.cycles.data.UserListBasic
// IMPORTANTE: Importamos lo que moviste a ListComponent.kt
import com.example.cycles.ui.components.ListCreateDialog
import com.example.cycles.ui.components.availableIcons
import com.example.cycles.ui.components.defaultIcon
import com.example.cycles.viewmodel.ListsViewModel

// Enum para las pestañas de esta pantalla
enum class ListsMainTab(val title: String) {
    DISCOVERY("Explorar"),
    MY_COLLECTION("Mi Colección")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    navController: NavController,
    viewModel: ListsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(ListsMainTab.DISCOVERY) }

    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadLists()
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                // 1. Barra de Búsqueda Global
                Box(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                        placeholder = { Text("Buscar listas, tags o géneros...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, null)
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                }

                // 2. Tabs Principales (Explorar vs Mi Colección)
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            height = 3.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)) }
                ) {
                    ListsMainTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    tab.title,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 16.sp
                                )
                            }
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // El FAB solo aparece en la pestaña de "Mi Colección"
            if (selectedTab == ListsMainTab.MY_COLLECTION) {
                FloatingActionButton(
                    onClick = {
                        viewModel.clearError()
                        showCreateDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Crear Lista")
                }
            }
        }
    ) { padding ->

        // Usamos el diálogo importado de ListComponent.kt
        if (showCreateDialog) {
            ListCreateDialog(viewModel = viewModel, onDismiss = { showCreateDialog = false })
        }

        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                ListsMainTab.DISCOVERY -> {
                    DiscoveryContent(navController)
                }
                ListsMainTab.MY_COLLECTION -> {
                    MyCollectionContent(state.lists, searchQuery, navController)
                }
            }
        }
    }
}

// ---------------------------------------------------------
// SECCIÓN 1: CONTENIDO DISCOVERY (RYM STYLE)
// ---------------------------------------------------------

@Composable
fun DiscoveryContent(navController: NavController) {
    // Datos Falsos para maquetar
    val categories = listOf("Horror", "Shoegaze", "Cine Negro", "Literatura Rusa", "Cyberpunk", "Jazz 50s", "Indie Game OST")
    val featuredLists = remember { List(5) { "Featured List #$it" } }
    val popularLists = remember { List(6) { "Popular List #$it" } }
    val recentLists = remember { List(8) { "User List Updated #$it" } }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. FEATURED LISTS
        item {
            Column {
                SectionHeader("Listas Destacadas", "Selección curada por editores")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(featuredLists) {
                        FeaturedListCard()
                    }
                }
            }
        }

        // 2. CATEGORIES
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("Por Categoría", null, modifier = Modifier.padding(bottom = 8.dp))
                // Fila 1
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(categories[0], Color(0xFFE57373))
                    CategoryChip(categories[1], Color(0xFFBA68C8))
                    CategoryChip(categories[2], Color(0xFF90A4AE))
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Fila 2
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryChip(categories[3], Color(0xFF7986CB))
                    CategoryChip(categories[4], Color(0xFF4DB6AC))
                }
            }
        }

        // 3. POPULAR LISTS
        item {
            Column {
                SectionHeader("Populares", "Tendencias de la semana")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(popularLists) {
                        PopularListCard()
                    }
                }
            }
        }

        // 4. RECENTLY UPDATED
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("Actualizadas Recientemente", null)
                Spacer(modifier = Modifier.height(8.dp))

                recentLists.forEach { title ->
                    RecentlyUpdatedItem(title)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                }
            }
        }
    }
}

// --- SUB-COMPONENTES DISCOVERY ---

@Composable
fun SectionHeader(title: String, subtitle: String?, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FeaturedListCard() {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.linearGradient(listOf(Color(0xFF1E88E5), Color(0xFF1565C0))))
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "CINE",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Top 100 Sci-Fi",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Curada por Cycles Staff",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
fun PopularListCard() {
    Card(
        modifier = Modifier.width(150.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                modifier = Modifier.size(50.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Star, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Indie Rock 2024", fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("45 items", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                Text(" 1.2k likes", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun CategoryChip(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(50),
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = "#$text",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color.copy(alpha = 1f)
        )
    }
}

@Composable
fun RecentlyUpdatedItem(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(4.dp),
            color = Color.Gray.copy(alpha = 0.2f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.AutoMirrored.Filled.List, null, tint = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(title, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Row {
                Text("por Usuario123", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                Text(" • hace 2h", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

// ---------------------------------------------------------
// SECCIÓN 2: MI COLECCIÓN
// ---------------------------------------------------------

@Composable
fun MyCollectionContent(
    userLists: List<UserListBasic>,
    searchQuery: String,
    navController: NavController
) {
    val filteredLists = userLists.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    if (filteredLists.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.AutoMirrored.Filled.LibraryBooks, null, modifier = Modifier.size(60.dp), tint = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                Text("No se encontraron listas", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            item {
                Text(
                    "Mis Listas (${filteredLists.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(filteredLists, key = { it.listId }) { list ->
                RymStyleListCard(
                    list = list,
                    onClick = { navController.navigate("list_detail/${list.listId}") }
                )
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

// --- RymStyleListCard (Requerida aquí para MyCollectionContent) ---
@Composable
fun RymStyleListCard(list: UserListBasic, onClick: () -> Unit) {
    // Usamos los iconos importados de ListComponent.kt
    val icon = availableIcons[list.iconName] ?: defaultIcon
    val listColor = try { Color(list.colorHex.toColorInt()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(60.dp),
            shape = RoundedCornerShape(4.dp),
            color = listColor.copy(alpha = 0.2f),
            border = BorderStroke(1.dp, listColor.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = listColor, modifier = Modifier.size(28.dp))
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text("${list.itemCount} items", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Pública", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}