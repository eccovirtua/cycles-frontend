package com.example.cycles.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.cycles.data.SearchResultItem
import com.example.cycles.navigation.AppDomain
import com.example.cycles.navigation.Screen
import com.example.cycles.ui.components.CyclesTitleComposable
import com.example.cycles.ui.components.DashboardPreviewSection
import com.example.cycles.ui.components.DomainSelectionDialog
import com.example.cycles.ui.components.InteractiveRecommendationCard
import com.example.cycles.ui.components.ListVerticalItem
import com.example.cycles.ui.components.ReviewListItem
import com.example.cycles.viewmodel.HomeViewModel



@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MoviesHomeScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onTitleClick: () -> Unit
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    // 1. CONECTAMOS CON EL ESTADO REAL
    val homeState by homeViewModel.uiState.collectAsState()

    // Estado del diálogo de cambio de dominio
    var showDomainDialog by remember { mutableStateOf(false) }

    // Configuración específica para CINE
    val currentDomain = AppDomain.MOVIES
    val interactiveRoute = Screen.InteractiveMovies.route
    val domainItemName = "película"

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.RESUMED) {
//            homeViewModel.loadUsageStatus()
            // Recargamos los datos del home si están vacíos
            if (homeState.topRatedList.isEmpty()) {
                homeViewModel.loadHomeData()
            }
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Películas", "Reseñas", "Listas", "Dashboard")

    // --- MANTENEMOS LOS MOCKS SOLO PARA LAS TABS QUE AÚN NO TIENEN BACKEND ---
    val mockReviews = remember {
        List(10) { index ->
            Triple("Pelicula Title $index", (1..5).random() + 0.5, "Usuario $index")
        }
    }
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CyclesTitleComposable(
                        titleText = "Recommendr (Movies)",
                        onTitleClick = onTitleClick,
                        onEditClick = { showDomainDialog = true }
                    )
                }
            }

            // 2. TARJETA SESIONES / INTERACTIVA
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    InteractiveRecommendationCard(
                        domainName = domainItemName,
                        onClick = {
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

            item { Spacer(modifier = Modifier.height(14.dp)) }

            // 4. CONTENIDO VARIABLE
            when (selectedTabIndex) {
                0 -> { // TAB PELÍCULAS (AHORA CON DATOS REALES)

                    if (homeState.isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else if (homeState.error != null) {
                        item {
                            Text(
                                text = "Error: ${homeState.error}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        // SECCIÓN 1: RECOMENDADOS GLOBALMENTE (Top Rated)
                        item {
                            HorizontalSection(
                                title = "Recomendados globalmente",
                                items = homeState.topRatedList, // Pasamos la lista real
                                onItemClick = { itemId ->
                                    navController.navigate("movie_detail/$itemId")
                                }
                            )
                        }

                        // SECCIÓN 2: RECOMENDADO PARA TI (For You - Random Placeholder)
                        item {
                            HorizontalSection(
                                title = "Recomendado para ti",
                                items = homeState.forYouList,
                                onItemClick = { itemId ->
                                    navController.navigate("movie_detail/$itemId")
                                }
                            )
                        }

                        // SECCIÓN 3: NUEVOS LANZAMIENTOS (New Releases)
                        item {
                            HorizontalSection(
                                title = "Nuevos lanzamientos",
                                items = homeState.newReleasesList,
                                onItemClick = { itemId ->
                                    navController.navigate("movie_detail/$itemId")
                                }
                            )
                        }
                    }
                }

                1 -> { // TAB RESEÑAS (MOCK)
                    items(mockReviews) { (title, rating, user) ->
                        ReviewListItem(title, rating, user)
                    }
                }

                2 -> { // TAB LISTAS (MOCK)
                    items(mockLists) { listName ->
                        ListVerticalItem(listName)
                    }
                }

                3 -> { // DASHBOARD
                    item {
                        DashboardPreviewSection(navController)
                    }
                }
            }
        }
    }

    if (showDomainDialog) {
        DomainSelectionDialog(
            currentDomain = currentDomain,
            onDismiss = { showDomainDialog = false },
            onDomainSelected = { newDomain ->
                showDomainDialog = false
                if (newDomain != currentDomain) {
                    navController.navigate(newDomain.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        )
    }
}

// --- ACTUALIZACIÓN NECESARIA DEL COMPONENTE HORIZONTAL SECTION ---
// Como cambiamos de List<String> a List<SearchResultItem>, necesitamos este adaptador
// Copia esto en tu archivo de componentes o al final de este archivo.

@Composable
fun HorizontalSection(
    title: String,
    items: List<SearchResultItem>,
    onItemClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .clickable { onItemClick(item.itemId) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

