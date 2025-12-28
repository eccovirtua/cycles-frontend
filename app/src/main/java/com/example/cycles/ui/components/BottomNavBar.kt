package com.example.cycles.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cycles.navigation.bottomNavItems
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun BottomNavBar(navController: NavHostController, modifier: Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        shadowElevation = 15.dp,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    ) {
        NavigationBar(
            modifier = Modifier.height(91.dp),
            containerColor = Color.Transparent
        ) {
            bottomNavItems.forEach { item ->
                // Usamos hierarchy para saber si estamos en una sub-pantalla de esa sección
                val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true

                NavigationBarItem(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label, modifier = Modifier.offset(y = 3.dp)) },
                    selected = isSelected,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = {
                        // 1. Navegar a la ruta
                        navController.navigate(item.route) {
                            // 2. PopUpTo: Limpia la pila hasta el inicio del grafo para no acumular pantallas
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // 3. Evita duplicados si das clic muchas veces
                            launchSingleTop = true
                            // 4. Restaura el estado (scroll, inputs) al volver
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}