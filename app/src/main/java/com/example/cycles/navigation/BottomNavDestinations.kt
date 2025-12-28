package com.example.cycles.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(Screen.HomeMovies.route, "Inicio", Icons.Filled.Tv)

    // 🎯 LISTAS
    object Lists : BottomNavItem("lists_route", "Listas", Icons.Filled.Bookmarks)

    // 🎯 PERFIL
    object Profile : BottomNavItem(Screen.Profile.route, "Perfil", Icons.Filled.AccountBox)

    // 🎯 BÚSQUEDA
    object Search : BottomNavItem(Screen.Search.route, "Buscar", Icons.Filled.Search)
}

// Lista de ítems a mostrar en la NavigationBar
val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Search,
    BottomNavItem.Lists,
    BottomNavItem.Profile

)