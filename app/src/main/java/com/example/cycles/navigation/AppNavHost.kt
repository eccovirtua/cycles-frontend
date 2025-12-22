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
import com.example.cycles.ui.screens.ItemDetailScreen
import com.example.cycles.ui.screens.ListDetailScreen
import com.example.cycles.ui.screens.ListsScreen
import com.example.cycles.ui.screens.RegisterScreen
import com.example.cycles.ui.screens.SearchScreen
import com.example.cycles.ui.screens.UserProfileScreen
import com.example.cycles.ui.screens.WelcomeScreen
import kotlinx.coroutines.launch

private const val TRANSITION_DURATION = 220

// animaciones
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
    val scope = rememberCoroutineScope()

    NavHost(
        navController,
        startDestination = Screen.Home.route,
    ) {
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
            SearchScreen(
                onItemClick = { itemId, itemType ->
                    // Navega a la pantalla de detalle, pasando el ID y Tipo
                    navController.navigate(Screen.ItemDetail.createRoute(itemId, itemType))
                }
            )
        }

        composable(
            route = Screen.ItemDetail.route,
            arguments = listOf(
                navArgument("itemId") { type = NavType.StringType },
                navArgument("itemType") { type = NavType.StringType }
            ),
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            val itemTypeString = backStackEntry.arguments?.getString("itemType") ?: ""
            val itemType = try {
                com.example.cycles.data.ItemType.valueOf(itemTypeString)
            } catch (_: IllegalArgumentException) {
                com.example.cycles.data.ItemType.BOOK // Fallback
            }
            ItemDetailScreen(
                itemId = itemId,
                itemType = itemType,
                onBack = { navController.popBackStack() }
            )
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
        ) { backStackEntry ->
            DashboardScreen()
        }

        composable(
            route = "lists_route",
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            ListsScreen(navController = navController)
        }

        composable(
            route = "list_detail/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.StringType }),
            enterTransition = { slideInFromRight },
            exitTransition = { slideOutToLeft },
            popEnterTransition = { slideInFromLeft },
            popExitTransition = { slideOutToRight }
        ) {
            ListDetailScreen(navController = navController)
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

        composable(
            route = Screen.ForgotPassword.route,
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
            popExitTransition = { slideOutToBottom }
        ) {
            ForgotPasswordScreen(navController)
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
//            ResetPasswordScreen(navController, emailArg, codeArg)
        }
        composable(
            route = "choose_username_screen/{age}",
            arguments = listOf(navArgument("age") { type = NavType.IntType }),
            enterTransition = { slideInFromBottom },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) },
            popExitTransition = { slideOutToBottom }
        ) {
            ChooseUsernameScreen(navController,paddingValues)
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
                navController = navController,
                onBackClick = { navController.popBackStack() },
                screenPadding = profilePadding,
                onLogoutClick = {
                    scope.launch {
//                        LoginViewModel.logout()
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