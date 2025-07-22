package com.example.cycles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.cycles.navigation.AppNavHost
import com.example.cycles.ui.theme.CyclesTheme
//import com.example.cycles.ui.screens.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            //aplica el tema global 'cyclestheme'
            CyclesTheme {
                //crea y recuerda un navHostController que gestiona la pila de pantallas y la navegacion entre rutas en este contexto de compose
                val navController = rememberNavController()
                //llamada a la función appnavhost, donde se asocia cada ruta a una pantalla (screen)
                AppNavHost(navController = navController)
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CyclesTheme {
        Greeting("Android")
    }
}