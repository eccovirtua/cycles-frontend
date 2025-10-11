package com.example.cycles.ui.components

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
fun BottomNavBar(navController: NavHostController, modifier: Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val CompactBarHeight = 24.dp
    val aggressiveVerticalPadding = 2.dp


    Surface(
        // 🎯 Aplicar el Modifier que viene de MainActivity (que incluye el padding flotante)
        modifier = modifier.fillMaxWidth(),


        // ✅ AHORA SÍ: Aplicamos los bordes redondeados a la Surface
        shape = RoundedCornerShape(0.dp),

        // Aplicamos la elevación para una sombra flotante
        shadowElevation = 15.dp,

        // Mantenemos el mismo color de fondo
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    ) {

        NavigationBar(
            modifier = Modifier.height(108.dp),

            containerColor = Color.Transparent
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route

                val compactContentPadding = PaddingValues(
                    horizontal = 4.dp,
                    vertical = aggressiveVerticalPadding // ⬅️ Padding vertical extremadamente reducido
                )



                NavigationBarItem(
                    modifier = Modifier
                        .height(IntrinsicSize.Min),
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label
                        )
                    },
                    label = { Text(item.label,modifier = Modifier.offset(y = (3).dp)) },
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}