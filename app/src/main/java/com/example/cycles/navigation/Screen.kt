package com.example.cycles.navigation

// Importa el Enum que necesitas para la función createRoute
import com.example.cycles.data.ItemType

sealed class Screen(val route: String) {

    data object Welcome : Screen("welcome")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object VerifyCode : Screen("verify_code/{email}")
    data object ResetPassword : Screen("reset_password/{email}/{code}")

    data object ChooseUsername : Screen("choose_username/{token}") {
        // Función helper para generar la ruta con valor real
        fun createRoute(token: String) = "choose_username/$token"
    }

    data object Home : Screen("home")

    data object Search : Screen("search")

    data object InteractiveMusic : Screen("interactive_music")
    data object InteractiveBooks : Screen("interactive_books")
    data object InteractiveMovies : Screen("interactive_movies")
    data object Profile : Screen("profile_route")
    data object EditProfile : Screen("edit_profile_route")
    data object Dashboard : Screen("dashboard")

    data object ItemDetail : Screen("item_detail/{itemId}/{itemType}") {
        // Esta función construye la ruta con los argumentos
        fun createRoute(itemId: String, itemType: ItemType): String {
            // Usa .name para convertir el enum (ej: BOOK) a un String
            return "item_detail/$itemId/${itemType.name}"
        }
    }
}