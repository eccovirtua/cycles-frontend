package com.example.cycles.navigation
//"NavHost"
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cycles.ui.screens.ChooseUsernameScreen
import com.example.cycles.ui.screens.LoginScreen
import com.example.cycles.ui.screens.RegisterScreen
import com.example.cycles.ui.screens.WelcomeScreen
import com.example.cycles.ui.screens.ForgotPasswordScreen
import com.example.cycles.ui.screens.ResetPasswordScreen
import com.example.cycles.ui.screens.VerifyCodeScreen


//defines las rutas en código con un NavHost. AppNavHost es tu “mapa” de pantallas.
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
        //ruta de pantalla principal
        composable("welcome") {
            WelcomeScreen(navController)
        }

        //login
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }

        //register
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }

        //forgot password
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(navController) }


//        //verificar codigo de resetpassword
//        composable(Screen.VerifyCode.route) {
//            VerifyCodeScreen(navController)
//        }
//
//        //resetpassword
//        composable(Screen.ResetPassword.route) {
//            ResetPasswordScreen(navController)
//        }

//        composable( //nav arg para saber QUE MAIl se tiene que verificar
//            "verify_code/{email}",
//            arguments = listOf(navArgument("email") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val emailArg = backStackEntry.arguments!!.getString("email")!!
//            VerifyCodeScreen(navController, email = URLDecoder.decode(emailArg, "UTF-8"))
//        }

        composable(
            route = Screen.VerifyCode.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { back ->
            val email = back.arguments!!.getString("email")!!
            VerifyCodeScreen(navController, email)
        }

        //VerifyCode recibe el email
//        composable(
//            route = "verify_code/{email}",
//            arguments = listOf(navArgument("email") { type = NavType.StringType })
//        ) { back ->
//            val emailArg = back.arguments!!.getString("email")!!
//            VerifyCodeScreen(
//                navController = navController,
//                email = URLDecoder.decode(emailArg, "UTF-8")
//            )
//        }

        //ResetPassword recibe email y code
//        composable(
//            route = "reset_password/{email}/{code}",
//            arguments = listOf(
//                navArgument("email") { type = NavType.StringType },
//                navArgument("code")  { type = NavType.StringType }
//            )
//        ) { back ->
//            val emailArg = URLDecoder.decode(back.arguments!!.getString("email")!!, "UTF-8")
//            val codeArg  = back.arguments!!.getString("code")!!
//            ResetPasswordScreen(
//                navController = navController,
//                email = emailArg,
//                code  = codeArg
//            )
//        }

        composable(
            route = Screen.ResetPassword.route,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("code")  { type = NavType.StringType }
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
            ChooseUsernameScreen(navController, token)
        }


//        composable("choose_username/{token}",
//            arguments = listOf(navArgument("token")
//            {
//                type = NavType.StringType
//            })
//            ) { backStackEntry ->
//            val tokenArg = backStackEntry.arguments!!.getString("token")!!
//            ChooseUsernameScreen(navController = navController, token = tokenArg)
//        }

        composable("home") {
            // Un Composable vacío para prueba
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("¡Bienvenido a Home!")
            }
        }







    }//esta llave cierra el bloque de codigo de las rutas!
}
