package com.example.cycles.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class AppDomain(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    MOVIES("Películas", "home_movies", Icons.Filled.LocalMovies),
    BOOKS("Libros", "home_books", Icons.Filled.Book),
    MUSIC("Música", "home_music", Icons.Filled.MusicNote)
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MusicHomeScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onTitleClick: () -> Unit // Acción al clicar el texto "Recommendr" (Easter egg o refresh)
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.uiState.collectAsState()

    // Estado del diálogo de cambio de dominio
    var showDomainDialog by remember { mutableStateOf(false) }

    val currentDomain = AppDomain.MUSIC

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            homeViewModel.loadUsageStatus()
        }
    }

    val interactiveRoute = "interactive_music" // Ruta al recomendador de películas
    val domainItemName = "musica" // Para el texto "Descubre tu próximo..."

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Películas", "Reseñas", "Listas", "Dashboard")

    // --- DATOS FALSOS ---
    val mockPosters = remember { List(10) { "https://picsum.photos/300/450?random=$it" } }

    // Datos falsos para Reseñas Verticales
    val mockReviews = remember {
        List(10) { index ->
            Triple("Pelicula Title $index", (1..5).random() + 0.5, "Usuario $index")
        }
    }

    // Datos falsos para Listas Verticales
    val mockLists = remember { List(8) { index -> "Top Horror 202$index" } }

    Scaffold(
        modifier = Modifier.padding(paddingValues),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            // 1. HEADER
            item {
                Column(

                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CyclesTitleComposable(
                        onTitleClick = onTitleClick,
                        onEditClick = { showDomainDialog = true } // Abre el popup
                    )
                }
            }

            // 2. TARJETA SESIONES
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    InteractiveRecommendationCard(
                        domainName = domainItemName,
                        onClick = {
                            // Navegar a la pantalla de "Tinder" de películas
                            navController.navigate(interactiveRoute)
                        }
                    )
                }
            }

            // 3. STICKY TABS
            stickyHeader {
                Surface(
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 2.dp
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        edgePadding = 16.dp,
                        containerColor = Color.Transparent,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTabIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Espaciador
            item { Spacer(modifier = Modifier.height(14.dp)) }

            // 4. CONTENIDO VARIABLE SEGÚN TAB
            when (selectedTabIndex) {
                0 -> { // TAB PELÍCULAS (Horizontal)
                    item { HorizontalSection("Recomendados globalmente", mockPosters) { navController.navigate("interactive_movies") } }
                    item { HorizontalSection("Recomendado para ti", mockPosters.reversed()) { } }
                    item { HorizontalSection("Nuevos lanzamientos", mockPosters.shuffled()) { } }
                }

                1 -> { // TAB RESEÑAS (Vertical - Reutilizando estilo)
                    items(mockReviews) { (title, rating, user) ->
                        // Aquí usamos un item de lista vertical
                        ReviewListItem(title, rating, user)
                    }
                }

                2 -> { // TAB LISTAS (Vertical)
                    items(mockLists) { listName ->
                        ListVerticalItem(listName)
                    }
                }

                3 -> { // DASHBOARD
                    item { DashboardPreviewSection(navController) }
                }
            }
        }
    }

    // --- POPUP DIALOG PARA CAMBIAR DOMINIO ---
    if (showDomainDialog) {
        DomainSelectionDialog(
            currentDomain = currentDomain,
            onDismiss = { showDomainDialog = false },
            onDomainSelected = { newDomain ->
                showDomainDialog = false

                if (newDomain != currentDomain) {
                    // Aquí usamos las rutas definidas en Screen.kt
                    val targetRoute = when(newDomain) {
                        AppDomain.MOVIES -> Screen.HomeMovies.route
                        AppDomain.BOOKS -> Screen.HomeBooks.route
                        AppDomain.MUSIC -> Screen.HomeMusic.route
                    }

                    navController.navigate(targetRoute) {
                        // Limpia el stack para no acumular pantallas
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun CyclesTitleComposable(
    onTitleClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val baseStyle = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Black)
    val gradientBrush = Brush.linearGradient(colors = listOf(Color(0xFFE53935), Color(0xFFD81B60)))
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleFactor by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1.0f, label = "scale")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "Recommendr (Movies)",
            style = baseStyle.copy(brush = gradientBrush),
            modifier = Modifier
                .scale(scaleFactor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { scope.launch { delay(100); onTitleClick() } }
                )
                .statusBarsPadding()
        )

        Spacer(modifier = Modifier.width(8.dp))

        // --- AQUÍ ESTÁ EL LÁPIZ ---
        IconButton(
            onClick = onEditClick,
            modifier = Modifier.statusBarsPadding().size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Cambiar dominio",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DomainSelectionDialog(
    currentDomain: AppDomain,
    onDismiss: () -> Unit,
    onDomainSelected: (AppDomain) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Selecciona un Dominio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppDomain.entries.forEach { domain ->
                    val isSelected = domain == currentDomain

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isSelected) { onDomainSelected(domain) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = domain.icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.Gray else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = domain.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Gray else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// --- ITEMS PARA LAS LISTAS VERTICALES ---

@Composable
fun ReviewListItem(title: String, rating: Double, user: String) {
    Column(modifier = Modifier.fillMaxWidth().clickable { }) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Placeholder imagen
            Surface(
                modifier = Modifier.size(60.dp),
                color = Color.Gray,
                shape = RoundedCornerShape(4.dp)
            ) {}

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                // Estrellitas simples
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    Text(text = "$rating", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Reseña por $user", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Text(text = "Una película increíble...", maxLines = 2, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodyMedium)
            }
        }
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun ListVerticalItem(listName: String) {
    ListItem(
        headlineContent = { Text(listName, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text("12 elementos • Actualizado hace 2h", style = MaterialTheme.typography.bodySmall) },
        leadingContent = {
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(40.dp),
                content = { Box(contentAlignment = Alignment.Center) { Icon(Icons.AutoMirrored.Filled.FormatListBulleted, null) } }
            )
        },
        modifier = Modifier.clickable { }
    )
    HorizontalDivider(thickness = 0.5.dp, modifier = Modifier.padding(start = 16.dp))
}


// --- RESTO DE COMPONENTES ORIGINALES (HorizontalSection, PosterCard, etc.) ---
@Composable
fun HorizontalSection(
    title: String,
    images: List<String>,
    onSeeAllClick: () -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 32.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { onSeeAllClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ver todo",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )
        }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(images) { imageUrl ->
                PosterCard(imageUrl)
            }
        }
    }
}

@Composable
fun PosterCard(imageUrl: String) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(110.dp)
            .aspectRatio(0.67f)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DashboardPreviewSection(navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(200.dp)
            .clickable { navController.navigate("dashboard") },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ir a tu Dashboard Completo", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate("dashboard") }) {
                    Text("Ver Estadísticas")
                }
            }
        }
    }
}

@Composable
fun InteractiveRecommendationCard(
    domainName: String, // "película", "libro", "álbum"
    onClick: () -> Unit
) {
    // Animación suave de escala al pulsar (opcional, para dar feedback táctil)
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "scale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp) // Un poco más alto para destacar
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Un gradiente para que se vea "inteligente" o "tecnológico"
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF2196F3), // Azul
                            Color(0xFF9C27B0)  // Violeta
                        )
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Descubre tu próximo $domainName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Recomendaciones con IA",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                // Icono de "Magia" o "IA"
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = androidx.compose.foundation.shape.CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward, // O Icons.Filled.AutoAwesome
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}