package com.example.cycles.navigation


sealed class Screen(val route: String) {


    data object Welcome : Screen("welcome")

    data object Login          : Screen("login")

    data object Register       : Screen("register")

    data object ForgotPassword : Screen("forgot_password")

    data object VerifyCode     : Screen("verify_code/{email}")
    data object ResetPassword  : Screen("reset_password/{email}/{code}")

    data object ChooseUsername : Screen("choose_username/{token}") {
        // Función helper para generar la ruta con valor real
        fun createRoute(token: String) = "choose_username/$token"

    }
    data object Home           : Screen("home")
    data object Search         : Screen("search")
    data object InteractiveMusic   : Screen("interactive_music")
    data object InteractiveBooks   : Screen("interactive_books")
    data object InteractiveMovies  : Screen("interactive_movies")
    data object Profile : Screen("profile_route")

    data object EditProfile : Screen("edit_profile_route")

    data object Dashboard : Screen("dashboard")

    // añadir profile, home, en el futuro.
}
