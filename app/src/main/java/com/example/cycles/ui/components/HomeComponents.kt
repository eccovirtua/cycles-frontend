package com.example.cycles.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.cycles.navigation.AppDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


enum class SearchFilter(val label: String, val icon: ImageVector) {
    ALL("todo", Icons.Default.Search),
    MOVIES("películas", Icons.Default.Movie),
    BOOKS("libros", Icons.Default.Book),
    MUSIC("música", Icons.Default.MusicNote),
    USERS("usuarios", Icons.Default.Person),
    LISTS("listas", Icons.AutoMirrored.Filled.List)
}

@Composable
fun CyclesTitleComposable(
    titleText: String, // Texto dinámico ("Recommendr Music", etc)
    gradientColors: List<Color> = listOf(Color(0xFFE53935), Color(0xFFD81B60)), // Por defecto rojo (Movies)
    onTitleClick: () -> Unit,
    onEditClick: () -> Unit
) {
    val baseStyle = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Black)
    // Usamos los colores que vienen por parámetro
    val gradientBrush = Brush.linearGradient(colors = gradientColors)

    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scaleFactor by animateFloatAsState(targetValue = if (isPressed) 0.95f else 1.0f, label = "scale")

    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = titleText, // Usamos la variable
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

// 2. SIN CAMBIOS: Este componente ya era lo suficientemente genérico
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

@Composable
fun ReviewListItem(title: String, rating: Double, user: String) {
    Column(modifier = Modifier.fillMaxWidth().clickable { }) {
        Row(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(60.dp),
                color = Color.Gray,
                shape = RoundedCornerShape(4.dp)
            ) {}

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(text = title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
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

// 3. MODIFICADO: Acepta el aspectRatio y lo pasa hacia abajo
@Composable
fun HorizontalSection(
    title: String,
    images: List<String>,
    posterAspectRatio: Float = 0.67f, // Valor por defecto (Pelis/Libros)
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
                // Pasamos el ratio aquí
                PosterCard(imageUrl, aspectRatio = posterAspectRatio)
            }
        }
    }
}

// 4. MODIFICADO: El cambio más importante para portadas vs posters
@Composable
fun PosterCard(
    imageUrl: String,
    aspectRatio: Float = 0.67f // 0.67f es 2:3 (Cine/Libros). Usa 1.0f para Música.
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .width(110.dp)
            .aspectRatio(aspectRatio) // Aquí se aplica la magia
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

// 5. MODIFICADO: Acepta colores para el gradiente según el dominio
@Composable
fun InteractiveRecommendationCard(
    domainName: String,
    gradientColors: List<Color> = listOf(Color(0xFF2196F3), Color(0xFF9C27B0)), // Por defecto Azul/Violeta
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1f, label = "scale")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .scale(scale)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Gradiente parametrizable
                    Brush.horizontalGradient(colors = gradientColors)
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

                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}