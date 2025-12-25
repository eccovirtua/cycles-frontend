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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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

    // Estado para controlar qué pestaña está activa (0: Libros, 1: Música, 2: Películas)
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Lista de pestañas
    val tabs = listOf(
        ProfileTab("Libros", Icons.Default.Book),
        ProfileTab("Música", Icons.Default.MusicNote),
        ProfileTab("Películas", Icons.Default.Movie)
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
        contentWindowInsets = WindowInsets(0.dp) // Permitimos que el contenido llegue hasta arriba (detras status bar)
    ) { innerPadding ->

        // BOX PRINCIPAL: Permite superponer elementos (Z-Index)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = innerPadding.calculateBottomPadding()
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 150.dp) // Espacio extra al final
            ) {
                // --- Header Item ---
                item {
                    ProfileHeaderContent(state, onEditClick)
                }
                stickyHeader {
                    Surface(
                        color = MaterialTheme.colorScheme.background, // Color de fondo para que no sea transparente al pegar
                        modifier = Modifier.fillMaxWidth()
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

                items(20) { index ->
                    val category = tabs[selectedTabIndex].title
                    ListItem(
                        headlineContent = { Text("$category Item #$index") },
                        supportingContent = { Text("Detalle del contenido de $category") },
                        leadingContent = {
                            Icon(
                                imageVector = tabs[selectedTabIndex].icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        },
                        modifier = Modifier.clickable { /* Acción al tocar item */ }
                    )
                    HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray.copy(alpha = 0.5f))
                }
            }

            // Botones de Navegación Personalizados
            // Usamos una Fila en la parte superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // Baja los botones para no tapar la hora/batería
                    .padding(horizontal = 16.dp, vertical = 8.dp), // Margen externo
                horizontalArrangement = Arrangement.SpaceBetween, // Uno a la izq, otro a la der
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Atrás
                CircleOverlayButton(
                    onClick = onBackClick,
                    icon = { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White) }
                )

                // Botón Logout
                CircleOverlayButton(
                    onClick = { viewModel.performLogout() },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White) }
                )
            }
        }
    }
}

// --- Componente Auxiliar para los botones flotantes ---
@Composable
fun CircleOverlayButton(
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    // Usamos Surface para darle forma redonda, borde y fondo semitransparente
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.3f), // Fondo semitransparente
        border = BorderStroke(1.dp, Color.DarkGray), // El borde gris oscuro
        modifier = Modifier.size(35.dp) // Tamaño del botón
    ) {
        Box(contentAlignment = Alignment.Center) {
            icon()
        }
    }
}


// --- Composables Auxiliares ---

@Composable
fun ProfileHeaderContent(state: UserProfileState, onEditClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Imagen de Portada
        Image(
            painter = rememberAsyncImagePainter(model = state.coverImageUrl),
            contentDescription = "Portada de perfil",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        // Contenido debajo de la portada
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // Fila: Foto de perfil y botón Editar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Foto de Perfil
                Image(
                    painter = rememberAsyncImagePainter(model = state.profileImageUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .border(3.dp, MaterialTheme.colorScheme.surface), // Borde circular
                    contentScale = ContentScale.Crop
                )
                // Botón Editar Perfil
                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.padding(bottom = 12.dp) // Alineación visual con la foto
                ) {
                    Text("Editar Perfil")
                }
            }
            // Columna: Nombre, Username, Bio
            Column(modifier = Modifier.offset(y = (-40).dp)) {
                Text(
                    text = state.name.ifEmpty { "DefaultNombre" },
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.username.ifEmpty { "@Defaultusername" },
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
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
