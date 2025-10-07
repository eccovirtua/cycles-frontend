package com.example.cycles.navigation
//Nav Host Para definir las rutas de las pantallas!
//conveniente usar route = "nombre de las pantallas" y NombreDeLaPantalla(navController)


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cycles.ui.screens.ChooseUsernameScreen
import com.example.cycles.ui.screens.FinalRecommendationsScreen
import com.example.cycles.ui.screens.LoginScreen
import com.example.cycles.ui.screens.RegisterScreen
import com.example.cycles.ui.screens.WelcomeScreen
import com.example.cycles.ui.screens.ForgotPasswordScreen
import com.example.cycles.ui.screens.HomeScreen
import com.example.cycles.ui.screens.ResetPasswordScreen
import com.example.cycles.ui.screens.VerifyCodeScreen
import com.example.cycles.ui.screens.InteractiveRecScreen


@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onTitleClick: () -> Unit
) {

    NavHost(
        navController,
        startDestination = Screen.Home.route,
    ) {
        //ruta de pantalla principal
        composable(Screen.Welcome.route) {
            WelcomeScreen(navController = navController, onTitleClick = onTitleClick, paddingValues = paddingValues)
        }

        //login
        composable(Screen.Login.route) {
            LoginScreen(navController, paddingValues)
        }

        //register
        composable(Screen.Register.route) {
            RegisterScreen(navController, paddingValues)
        }

        //forgot password
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController) }

        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { back ->
            val email = back.arguments!!.getString("email")!!
            VerifyCodeScreen(navController, email)
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code")   { type = NavType.StringType }
            )
        ) { back ->
            val emailArg = back.arguments!!.getString("email")!!
            val codeArg  = back.arguments!!.getString("code")!!
            ResetPasswordScreen(navController, emailArg, codeArg)
        }

        composable(
            route = Screen.ChooseUsername.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType })
        ) { back ->
            val token = back.arguments!!.getString("token")!!
            ChooseUsernameScreen(navController, token, paddingValues)
        }

        composable(Screen.Home.route) {
            HomeScreen(navController)
        }

        composable(Screen.InteractiveMusic.route) {
            InteractiveRecScreen(navController = navController, domain = "music")
        }
        composable(Screen.InteractiveBooks.route) {
            InteractiveRecScreen(navController = navController, domain = "book")
        }
        composable(Screen.InteractiveMovies.route) {
            InteractiveRecScreen(navController = navController, domain = "movie")
        }



        // Pantalla interactiva (dinámica)
        composable("interactive/{domain}") { backStackEntry ->
            val domain = backStackEntry.arguments?.getString("domain") ?: ""
            InteractiveRecScreen(
                domain = domain,
                navController = navController
            )
        }

        // Pantalla final (dinámica)
        composable(
            route = "final/{domain}/{sessionId}",
            arguments = listOf(
                navArgument("domain") { type = NavType.StringType },
                navArgument("sessionId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val domain = backStackEntry.arguments?.getString("domain") ?: return@composable
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable

            FinalRecommendationsScreen(
                domain = domain,
                sessionId = sessionId,
                navController = navController // <--- PASAMOS navController
            )
        }






    }//esta llave cierra el bloque de codigo de las rutas!
}
