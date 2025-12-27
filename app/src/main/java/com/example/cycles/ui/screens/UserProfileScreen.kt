package com.example.cycles.ui.screens



import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp
import kotlin.math.floor

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

    val (showLogoutDialog, setShowLogoutDialog) = remember { mutableStateOf(false) }

    // --- DATOS FALSOS ---
    val fakeFavorites = remember { List(53) { "https://picsum.photos/200?random=$it" } }


    val fakeReviews = remember {
        List(10) { index ->
            Triple(index, (1..5).random() + if(index % 2 == 0) 0.5 else 0.0, "21/01/2026")
        }
    }

    val fakeLists = remember { List(5) { it } }


    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        ProfileTab("Favoritos", Icons.Default.Favorite),
        ProfileTab("Ratings", Icons.AutoMirrored.Filled.StarHalf),
        ProfileTab("Listas", Icons.AutoMirrored.Filled.FormatListBulleted)
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
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
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
                        val chunkedItems = fakeFavorites.chunked(4)
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
                                val emptySlots = 4 - rowImages.size
                                repeat(emptySlots) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                    1 -> {
                        // RESEÑAS
                        items(items = fakeReviews) { (index, rating, date) ->
                            ReviewItemCard(
                                itemName = "Album/Pelicula #$index",
                                rating = rating, // Pasamos Double (ej: 3.5)
                                date = date,     // Pasamos fecha
                                reviewText = if (index % 2 == 0) "Una obra maestra absoluta..." else null,
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
                CircleOverlayButton(onClick = { setShowLogoutDialog(true) }, icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout", tint = Color.White) })            }
        }
    }
    // Dialogo usando el booleano (show) y el setter (setShow)
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { setShowLogoutDialog(false) },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres salir de tu cuenta?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        setShowLogoutDialog(false)
                        viewModel.performLogout()
                    }
                ) {
                    Text("Salir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowLogoutDialog(false) }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// --- Componentes Auxiliares ---

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
            // Fila: Foto y Botón Editar (Se mantiene igual para alinear la foto)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = state.profileImageUrl),
                    contentDescription = "Foto perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(3.dp, MaterialTheme.colorScheme.surface),
                    contentScale = ContentScale.Crop
                )
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text("Editar Perfil")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp),
                horizontalArrangement = Arrangement.SpaceBetween, // Espacio entre Info y Widget
                verticalAlignment = Alignment.Top
            ) {
                // IZQUIERDA: Info Usuario
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.username.ifEmpty { "@usuario" },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    // Fila de Edad y País
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.showAge) {
                            Text(
                                text = "${state.age}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 2. LÓGICA DEL SEPARADOR
                        // Solo mostramos el punto si la edad es visible Y hay un país que mostrar
                        if (state.showAge && state.country.isNotEmpty() && state.country != "Jupiter") {
                            Text(
                                text = " • ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // 3. LÓGICA DE PAÍS
                        if (state.country.isNotEmpty() && state.country != "Jupiter") {
                            Text(
                                text = state.country,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // DERECHA: Widget "Escuchando Ahora"
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.5f),
                    modifier = Modifier
                        .width(170.dp)
                        .height(65.dp) // Altura compacta
                        .clickable { /* Ir al item */ }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(6.dp)
                    ) {
                        // Icono animado o carátula pequeña
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq, // Icono de música
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(25.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = "Escuchando ahora: ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 9.sp
                            )
                            Text(
                                text = "Radiohead",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "In Rainbows",
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
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
fun ReviewItemCard(itemName: String, rating: Double, date: String, reviewText: String?, imageUrl: String) {
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
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // CABECERA DE LA RESEÑA: Título a la izq, Fecha a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween, // Esto separa Título y Fecha
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = itemName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f) // El título ocupa lo que necesite pero deja espacio
                )

                // FECHA A LA DERECHA
                Text(
                    text = date,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ESTRELLAS CON DECIMALES
            RatingStars(rating = rating)

            if (!reviewText.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = reviewText,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// COMPONENTE PARA ESTRELLAS CON DECIMALES
@Composable
fun RatingStars(rating: Double) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val fullStars = floor(rating).toInt()
        val hasHalfStar = (rating - fullStars) >= 0.5

        repeat(5) { index ->
            val icon = when {
                index < fullStars -> Icons.Filled.Star // Estrella completa
                index == fullStars && hasHalfStar -> Icons.AutoMirrored.Filled.StarHalf // Media estrella
                else -> Icons.Outlined.StarOutline // Estrella vacía
            }

            val tint = if (index < fullStars || (index == fullStars && hasHalfStar))
                Color(0xFFFFC107) // Amarillo
            else
                Color.Gray // Gris

            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = tint
            )
        }

        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$rating",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
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
        trailingContent = { Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = null, tint = Color.Gray) },
        modifier = Modifier.clickable { }
    )
    HorizontalDivider(thickness = 0.5.dp)
}
