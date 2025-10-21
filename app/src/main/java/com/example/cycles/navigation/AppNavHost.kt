package com.example.cycles.navigation
//Nav Host Para definir las rutas de las pantallas!
//conveniente usar route = "nombre de las pantallas" y NombreDeLaPantalla(navController)


import androidx.compose.animation.core.tween // Necesario para el timing
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cycles.ui.screens.ChooseUsernameScreen
import com.example.cycles.ui.screens.DashboardScreen
import com.example.cycles.ui.screens.EditProfileScreen
import com.example.cycles.ui.screens.FinalRecommendationsScreen
import com.example.cycles.ui.screens.ForgotPasswordScreen
import com.example.cycles.ui.screens.HomeScreen
import com.example.cycles.ui.screens.InteractiveRecScreen
import com.example.cycles.ui.screens.LoginScreen
import com.example.cycles.ui.screens.RegisterScreen
import com.example.cycles.ui.screens.ResetPasswordScreen
import com.example.cycles.ui.screens.SearchScreen
import com.example.cycles.ui.screens.UserProfileScreen
import com.example.cycles.ui.screens.VerifyCodeScreen
import com.example.cycles.ui.screens.WelcomeScreen
import com.example.cycles.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

private const val TRANSITION_DURATION = 220

// 🎯 Definición de las transiciones estándar de deslizamiento horizontal
private val slideInFromRight = slideInHorizontally(
    initialOffsetX = { it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

private val slideOutToLeft = slideOutHorizontally(
    targetOffsetX = { -it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeOut(animationSpec = tween(TRANSITION_DURATION))

private val slideInFromLeft = slideInHorizontally(
    initialOffsetX = { -it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

private val slideOutToRight = slideOutHorizontally(
    targetOffsetX = { it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeOut(animationSpec = tween(TRANSITION_DURATION))

// 🎯 Definición de las transiciones modales (entrada/salida vertical)
private val slideInFromBottom = slideInVertically(
    initialOffsetY = { it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

private val slideOutToBottom = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(TRANSITION_DURATION)
) + fadeOut(animationSpec = tween(TRANSITION_DURATION))


@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onTitleClick: () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current
    val authViewModel: AuthViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()


    // ✅ Usamos NavHost estándar
    NavHost(
        navController,
        startDestination = Screen.Home.route,
    ) {

        // --- RUTAS PREDETERMINADAS (Deslizamiento Lateral) ---

        composable(
            route = Screen.Welcome.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            WelcomeScreen(navController = navController, onTitleClick = onTitleClick, paddingValues = paddingValues)
        }

        composable(
            route = Screen.Home.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            HomeScreen(navController, paddingValues, onTitleClick = onTitleClick)
        }

        composable(
            route = Screen.Search.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            SearchScreen()
        }

        // --- RUTAS MODALES (Aparecen desde Abajo, más apropiado para Auth) ---

        // login
        composable(
            route = Screen.Login.route,
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) }, // FadeOut para que la pantalla de fondo no se mueva
            popExitTransition = { slideOutToBottom }
        ) {
            LoginScreen(navController, paddingValues)
        }

        //dashboard
        composable(
            route = Screen.Dashboard.route,
            enterTransition = { slideInFromBottom},
            exitTransition = { fadeOut(tween(TRANSITION_DURATION))},
            popExitTransition = { slideOutToBottom }

        ) {
            DashboardScreen()
        }


        composable(
            route = "dashboard?animate={animate}",
            arguments = listOf(navArgument("animate") {
                type = NavType.BoolType
                defaultValue = false
            })
        ) { backStackEntry -> // 'backStackEntry' might still be needed for other things
            DashboardScreen() // Just call the screen, ViewModel gets the arg
        }


        // register
        composable(
            route = Screen.Register.route,
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
            popExitTransition = { slideOutToBottom }
        ) {
            RegisterScreen(navController, paddingValues)
        }

        // forgot password (y sus flujos)
        composable(
            route = Screen.ForgotPassword.route,
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
            popExitTransition = { slideOutToBottom }
        ) {
            ForgotPasswordScreen(navController)
        }

        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType }),
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
            popExitTransition = { slideOutToBottom }
        ) { back ->
            val email = back.arguments!!.getString("email")!!
            VerifyCodeScreen(navController, email)
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code")   { type = NavType.StringType }
            ),
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
            popExitTransition = { slideOutToBottom }
        ) { back ->
            val emailArg = back.arguments!!.getString("email")!!
            val codeArg  = back.arguments!!.getString("code")!!
            ResetPasswordScreen(navController, emailArg, codeArg)
        }

        composable(
            route = Screen.ChooseUsername.route,
            arguments = listOf(navArgument("token") { type = NavType.StringType }),
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
            popExitTransition = { slideOutToBottom }
        ) { back ->
            val token = back.arguments!!.getString("token")!!
            ChooseUsernameScreen(navController, token, paddingValues)
        }

        // --- RUTAS INTERACTIVAS Y PERFIL (Deslizamiento Lateral) ---

        composable(
            route = Screen.InteractiveMusic.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            InteractiveRecScreen(navController = navController, domain = "music")
        }

        composable(
            route = Screen.InteractiveBooks.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            InteractiveRecScreen(navController = navController, domain = "book")
        }

        composable(
            route = Screen.InteractiveMovies.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            InteractiveRecScreen(navController = navController, domain = "movie")
        }


        // Pantalla interactiva (dinámica)
        composable(
            route = "interactive/{domain}",
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) { backStackEntry ->
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
            ),
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) { backStackEntry ->
            val domain = backStackEntry.arguments?.getString("domain") ?: return@composable
            val sessionId = backStackEntry.arguments?.getString("sessionId") ?: return@composable

            FinalRecommendationsScreen(
                domain = domain,
                sessionId = sessionId,
                navController = navController
            )
        }

        composable(
            route = "profile_route",
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            val profilePadding = PaddingValues(
                top = paddingValues.calculateTopPadding(),
                bottom = 0.dp,
                start = paddingValues.calculateStartPadding(layoutDirection),
                end = paddingValues.calculateEndPadding(layoutDirection)
            )


            UserProfileScreen(
                onBackClick = { navController.popBackStack() },
                screenPadding = profilePadding,
                onLogoutClick = {
                    scope.launch {
                        authViewModel.logout()

                        navController.navigate(Screen.Welcome.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                onEditClick = { navController.navigate(Screen.EditProfile.route) }
            )
        }

        composable(
            route = Screen.EditProfile.route,
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            EditProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}