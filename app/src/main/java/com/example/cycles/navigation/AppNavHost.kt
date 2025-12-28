package com.example.cycles.navigation
//Nav Host Para definir las rutas de las pantallas!
//conveniente usar route = "nombre de las pantallas" y NombreDeLaPantalla(navController)


import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cycles.ui.screens.BooksHomeScreen
import com.example.cycles.ui.screens.ChooseUsernameScreen
import com.example.cycles.ui.screens.DashboardScreen
import com.example.cycles.ui.screens.FinalRecommendationsScreen
import com.example.cycles.ui.screens.ForgotPasswordScreen
import com.example.cycles.ui.screens.InteractiveRecScreen
import com.example.cycles.ui.screens.ItemDetailScreen
import com.example.cycles.ui.screens.ListsScreen
import com.example.cycles.ui.screens.RegisterScreen
import com.example.cycles.ui.screens.SearchScreen
import com.example.cycles.ui.screens.UserProfileScreen
import com.example.cycles.ui.screens.WelcomeScreen
import com.example.cycles.ui.screens.EditProfileScreen
import com.example.cycles.ui.screens.MoviesHomeScreen
import com.example.cycles.ui.screens.MusicHomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onTitleClick: () -> Unit
) {
    val layoutDirection = LocalLayoutDirection.current

    NavHost(
        navController,
        startDestination = Screen.HomeMovies.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None }
    ) {
        composable(
            route = Screen.Welcome.route
        ) {
            WelcomeScreen(navController = navController, onTitleClick = onTitleClick, paddingValues = paddingValues)
        }

        composable(
            route = Screen.HomeMovies.route
        ) {
            // Asumiendo que renombraste tu HomeScreen actual a MoviesHomeScreen
            MoviesHomeScreen(navController, paddingValues, onTitleClick)
        }

        // --- HOME LIBROS ---
        composable(
            route = Screen.HomeBooks.route
        ) {
            // Tienes que crear este archivo/función
            BooksHomeScreen(navController, paddingValues, onTitleClick)
        }

        // --- HOME MÚSICA ---
        composable(
            route = Screen.HomeMusic.route
        ) {
            // Tienes que crear este archivo/función
            MusicHomeScreen(navController, paddingValues, onTitleClick)
        }

        composable(
            route = Screen.Search.route
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
            )
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
            route = Screen.Dashboard.route
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
            route = "lists_route"
        ) {
            ListsScreen(navController = navController)
        }

        composable(
            route = "list_detail/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.StringType })
        ) {
        }
        // register
        composable(
            route = Screen.Register.route
        ) {
            RegisterScreen(navController, paddingValues)
        }

        composable(
            route = Screen.ForgotPassword.route
        ) {
            ForgotPasswordScreen(navController)
        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code")   { type = NavType.StringType }
            ),
        ) { back ->
            val emailArg = back.arguments!!.getString("email")!!
            val codeArg  = back.arguments!!.getString("code")!!
//            ResetPasswordScreen(navController, emailArg, codeArg)
        }
        composable(
            route = Screen.ChooseUsername.route
        ) {
            ChooseUsernameScreen(navController,paddingValues)
        }

        // --- RUTAS INTERACTIVAS Y PERFIL (Deslizamiento Lateral) ---

        composable(
            route = Screen.InteractiveMusic.route
        ) {
            InteractiveRecScreen(navController = navController, domain = "music")
        }

        composable(
            route = Screen.InteractiveBooks.route
        ) {
            InteractiveRecScreen(navController = navController, domain = "book")
        }

        composable(
            route = Screen.InteractiveMovies.route
        ) {
            InteractiveRecScreen(navController = navController, domain = "movie")
        }


        // Pantalla interactiva (dinámica)
        composable(
            route = "interactive/{domain}",
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
            )
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
            route = "profile_route"
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
                onEditClick = { navController.navigate(Screen.EditProfile.route) }
            )
        }

        composable(
            route = Screen.EditProfile.route
        ) {
            EditProfileScreen(
                onBackClick = {
                    // Acción al tocar la flecha atrás o cancelar:
                    // Simplemente volvemos a la pantalla anterior (Perfil)
                    navController.popBackStack()
                }
            )
        }
    }
}