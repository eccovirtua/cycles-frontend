package com.example.cycles.ui.screens


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import com.example.cycles.navigation.AppDomain
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.HomeViewModel
import com.example.cycles.ui.components.CyclesTitleComposable
import com.example.cycles.ui.components.DashboardPreviewSection
import com.example.cycles.ui.components.DomainSelectionDialog
import com.example.cycles.ui.components.HorizontalSection
import com.example.cycles.ui.components.InteractiveRecommendationCard
import com.example.cycles.ui.components.ListVerticalItem
import com.example.cycles.ui.components.ReviewListItem

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
                        titleText = "Recommendr (Music)",
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
