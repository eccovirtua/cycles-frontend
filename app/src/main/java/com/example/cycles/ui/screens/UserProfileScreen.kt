package com.example.cycles.ui.screens



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
import androidx.compose.ui.platform.LocalLayoutDirection
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




// --- Pantalla Principal ---
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class) // Añadir ExperimentalFoundationApi
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


    LaunchedEffect(isLoggedOut) {
        if (isLoggedOut) {
            // Navegamos a la pantalla de bienvenida (Welcome)
            navController.navigate(Screen.Welcome.route) {
                // popUpTo(0) borra TODA la pila de pantallas anteriores.
                // Así, si el usuario presiona "Atrás" en el Welcome, se sale de la app
                // en lugar de volver al Perfil.
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Carga inicial y recarga de sección
    LaunchedEffect(Unit) {
        viewModel.loadUserProfileData()
        // Carga la sección inicial si aún no se ha cargado
        if (state.activeLists.isEmpty() && state.archivedLists.isEmpty() && state.favoriteItems.isEmpty()) {

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
                .height(150.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant), // Color de fondo placeholder
            contentScale = ContentScale.Crop
        )
        // Contenido debajo de la portada
        Column(modifier = Modifier.padding(horizontal = 6.dp)) {
            // Fila: Foto de perfil y botón Editar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-90).dp), // Superponer foto sobre portada
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween // Empuja el botón a la derecha
            ) {
                // Foto de Perfil
                Image(
                    painter = rememberAsyncImagePainter(model = state.profileImageUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp) // Reducir tamaño
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
                    IconButton(onClick = { viewModel.performLogout() }) {
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

            }

            // --- Section Content Item ---
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
//                        .heightIn(min = 300.dp) // Altura mínima para contenido
                        .padding(top = 8.dp) // Espacio después de las pestañas
                ) {
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
