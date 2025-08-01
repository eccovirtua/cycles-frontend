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

    data object MusicRecommendation: Screen("musicReco")



    // añadir profile, home, en el futuro.
}
