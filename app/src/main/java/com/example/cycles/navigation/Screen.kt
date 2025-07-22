package com.example.cycles.navigation


sealed class Screen(val route: String) {
//    data object Welcome        : Screen("welcome")
    data object Login          : Screen("login")
    data object Register       : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    // Luego podrás añadir Home, Profile, etc.
}
