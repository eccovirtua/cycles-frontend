package com.example.cycles.ui.screens



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.cycles.navigation.Screen
import com.example.cycles.viewmodel.UserProfileState
import com.example.cycles.viewmodel.UserProfileViewModel
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ProfileTab(val title: String, val icon: ImageVector)
// --- Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    screenPadding: PaddingValues,
    onEditClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val isLoggedOut by viewModel.isLoggedOut.collectAsState()

    // --- DATOS FALSOS PARA PRUEBA (Reemplazar con datos del ViewModel) ---
    // Generamos 53 items para probar que el grid funcione con filas incompletas al final
    val fakeFavorites = remember { List(53) { "https://picsum.photos/200?random=$it" } }
    val fakeReviews = remember { List(10) { it } } // Simula 10 reseñas
    val fakeLists = remember { List(5) { it } }    // Simula 5 listas
    // ---------------------------------------------------------------------

    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        ProfileTab("Favoritos", Icons.Default.Favorite), // Icono Corazón
        ProfileTab("Reseñas", Icons.Default.RateReview), // Icono Reseña
        ProfileTab("Listas", Icons.AutoMirrored.Filled.FormatListBulleted) // Icono Lista
    )

    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            navController.navigate(Screen.Welcome.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserProfileData()
    }


    Scaffold(
        contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Aplicamos solo el padding inferior del scaffold (navegación por gestos)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                // CORRECCIÓN: Sumamos el screenPadding para que el contenido no se corte abajo
                contentPadding = PaddingValues(
                    bottom = 20.dp + screenPadding.calculateBottomPadding(),
                    top = 0.dp
                )
            ) {
                // 1. HEADER
                item {
                    ProfileHeaderContent(state, onEditClick)
                }

                // 2. STICKY HEADER
                stickyHeader {
                    Surface(
                        color = MaterialTheme.colorScheme.background,
                        shadowElevation = 4.dp
                    ) {
                        TabRow(
                            selectedTabIndex = selectedTabIndex,
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.primary,
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        ) {
                            tabs.forEachIndexed { index, tab ->
                                Tab(
                                    selected = selectedTabIndex == index,
                                    onClick = { selectedTabIndex = index },
                                    text = { Text(tab.title) },
                                    icon = { Icon(tab.icon, contentDescription = null) }
                                )
                            }
                        }
                    }
                }

                // 3. CONTENIDO
                when (selectedTabIndex) {
                    0 -> { // FAVORITOS
                        val chunkedItems = fakeFavorites.chunked(5)
                        // CORRECCIÓN: Usamos 'items' explícitamente para evitar confusión con Int
                        items(items = chunkedItems) { rowImages ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 2.dp, vertical = 2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                rowImages.forEach { imageUrl ->
                                    Box(modifier = Modifier.weight(1f)) {
                                        FavoriteGridItem(imageUrl)
                                    }
                                }
                                val emptySlots = 5 - rowImages.size
                                repeat(emptySlots) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    1 -> { // RESEÑAS
                        items(items = fakeReviews) { index ->
                            ReviewItemCard(
                                itemName = "Album/Pelicula #$index",
                                rating = (1..5).random(),
                                reviewText = if (index % 2 == 0) "Una obra maestra..." else null,
                                imageUrl = "https://picsum.photos/200?random=$index"
                            )
                            HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }
                    2 -> { // LISTAS
                        items(items = fakeLists) { index ->
                            ListItemCard(
                                listName = "Mis favoritos 2024",
                                itemCount = (5..50).random(),
                                description = "Recopilación del año."
                            )
                        }
                    }
                }
            }

            // BOTONES FLOTANTES
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                CircleOverlayButton(onClick = onBackClick, icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White) })
                Spacer(modifier = Modifier.weight(1f))
                CircleOverlayButton(onClick = { viewModel.performLogout() }, icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = Color.White) })
            }
        }
    }
}

@Composable
fun CircleOverlayButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.3f),
        border = BorderStroke(1.dp, Color.DarkGray),
        modifier = Modifier.size(35.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            icon()
        }
    }
}

@Composable
fun ProfileHeaderContent(state: UserProfileState, onEditClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = rememberAsyncImagePainter(model = state.coverImageUrl),
            contentDescription = "Portada",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // CORRECCIÓN: Foto redonda con borde
                Image(
                    painter = rememberAsyncImagePainter(model = state.profileImageUrl),
                    contentDescription = "Foto perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape) // 1. Recortamos primero
                        .background(MaterialTheme.colorScheme.surface)
                        .border(3.dp, MaterialTheme.colorScheme.surface, CircleShape), // 2. Borde redondo
                    contentScale = ContentScale.Crop
                )
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text("Editar Perfil")
                }
            }
            Column(modifier = Modifier.offset(y = (-40).dp)) {
                Text(
                    text = state.name.ifEmpty { "Usuario" },
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
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun FavoriteGridItem(imageUrl: String) {
    Image(
        painter = rememberAsyncImagePainter(model = imageUrl),
        contentDescription = null,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.LightGray),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ReviewItemCard(itemName: String, rating: Int, reviewText: String?, imageUrl: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(12.dp)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = itemName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (index < rating) Color(0xFFFFC107) else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "$rating/5", style = MaterialTheme.typography.labelMedium)
            }
            if (!reviewText.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = reviewText, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ListItemCard(listName: String, itemCount: Int, description: String) {
    ListItem(
        headlineContent = { Text(listName, fontWeight = FontWeight.SemiBold) },
        supportingContent = { Text(description, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        leadingContent = {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = itemCount.toString(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        },
        trailingContent = { Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = Color.Gray) },
        modifier = Modifier.clickable { }
    )
    HorizontalDivider(thickness = 0.5.dp)
}
