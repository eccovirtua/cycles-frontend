package com.example.cycles.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.cycles.viewmodel.UserProfileState
import com.example.cycles.viewmodel.UserProfileViewModel

val profileSections = listOf("Listas", "Archivadas", "Favoritos")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    screenPadding: PaddingValues, // ⬅️ Este ya tiene bottom = 0.dp
    onLogoutClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }

    // 🛑 LÍNEA ELIMINADA: Ya no necesitamos calcular esto aquí,
    // ya que el LazyColumn lo gestionará con el padding del TopBar.
    // val bottomInsetsHeight = WindowInsets.systemBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.username, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = onLogoutClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        },
        // ✅ CLAVE 1: Aseguramos que el Scaffold NO inyecte padding adicional al NavHost
        contentWindowInsets = WindowInsets(0.dp)

    ) { innerPadding ->


        LazyColumn(
            // ✅ CLAVE 2: Aplicar el padding exterior (screenPadding) al contenedor principal
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = screenPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = screenPadding.calculateEndPadding(LocalLayoutDirection.current)
                ),

            contentPadding = PaddingValues(
                // ✅ El padding superior viene de la TopAppBar del Scaffold
                top = innerPadding.calculateTopPadding(),

                // 🛑 CLAVE 3: Usamos 0.dp para el padding inferior.
                // Esto permite que el LazyColumn se extienda hasta el borde inferior de la pantalla,
                // cubriendo el espacio que antes mostraba el fondo animado.
                bottom = 0.dp,
                start = 0.dp,
                end = 0.dp
            )
        ) {

            // 1. HEADER (Portada, Foto, Biografía, Estadísticas)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp)
                ) {

                    Spacer(Modifier.height(0.dp))

                    ProfileHeaderContent(state, onEditClick)

                    Spacer(Modifier.height(1.dp))
                }
            }

            // 2. SELECCIÓN DE SECCIONES (Pestañas) - STICKY
            stickyHeader {
                SectionTabs(
                    selectedTabIndex = state.sectionIndex,
                    onTabSelected = viewModel::onSectionSelected
                )
            }

            // 3. CONTENIDO DE LA SECCIÓN (Listas, Favoritos, etc.)
            item {
                SectionContent(state.sectionIndex)
            }
        }
    }
}


// --- Componentes Reutilizables ---

// 🎯 Separamos el contenido de la cabecera del Column principal.
@Composable
fun ProfileHeaderContent(state: UserProfileState, onEditClick: () -> Unit) {

    Column(modifier = Modifier.fillMaxWidth()) {
        // 1. IMAGEN DE PORTADA
        Image(
            painter = rememberAsyncImagePainter(model = state.coverImageUrl),
            contentDescription = "Portada de perfil",
            modifier = Modifier
                .fillMaxWidth()
                .height(177.dp)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Column(modifier = Modifier.padding(horizontal = 14.dp)) {
            // 2. FOTO DE PERFIL Y BOTÓN DE EDICIÓN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = state.profileImageUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(2.dp)
                        .background(Color.Black),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = onEditClick,
                    modifier = Modifier.padding(bottom = 30.dp)
                ) {
                    Text("Editar Perfil")
                }
            }

            // 3. NOMBRE, USUARIO y ESTADÍSTICAS
            Column(modifier = Modifier.offset(y = (-40).dp)) {
                Text(
                    text = state.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.username,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Biografía
                Text(
                    text = state.bio,
                    style = MaterialTheme.typography.bodyMedium
                )


                Spacer(modifier = Modifier.height(16.dp))

                // Seguidores y Seguidos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${state.followersCount}",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " Seguidores",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SectionTabs(selectedTabIndex: Int, onTabSelected: (Int) -> Unit) {
    // 🎯 Fila de pestañas que se mantendrá pegada al TopBar al hacer scroll (stickyHeader)
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
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
fun SectionContent(index: Int) {
    // 🎯 Área donde irá el contenido real (listas, favoritos, etc.)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp) // Altura mínima para asegurar scroll
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (index) {
            0 -> Text("Contenido de Listas del Usuario (futuras Listas)", color = MaterialTheme.colorScheme.onSurfaceVariant)
            1 -> Text("Contenido Archivadas (futuras Listas)", color = MaterialTheme.colorScheme.onSurfaceVariant)
            2 -> Text("Contenido de Favoritos (futuros Likes)", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Placeholder para que el LazyColumn tenga suficiente contenido para hacer scroll

    }
}
