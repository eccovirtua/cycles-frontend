package com.example.cycles


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.cycles.navigation.AppNavHost
import com.example.cycles.ui.theme.AnimatedBackground
import com.example.cycles.ui.theme.CyclesTheme
import com.example.cycles.ui.theme.ThemeColors
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.graphics.Color




// 1. ESTADO GLOBAL: El índice del tema actual (debe estar fuera de la clase)
val currentThemeIndex = mutableIntStateOf(0)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val activeColorScheme = ThemeColors[currentThemeIndex.intValue]

            val themeCycleAction = {
                currentThemeIndex.intValue = (currentThemeIndex.intValue + 1) % ThemeColors.size
            }

            // Aplica el tema global, pasando el esquema activo
            CyclesTheme(activeColorScheme) {
                val navController = rememberNavController()
                AnimatedBackground(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        modifier = Modifier.fillMaxSize()
                    ) { paddingValues ->


                        // Llama al host de navegación, pasando el controlador y la acción de clic
                        AppNavHost(
                            navController = navController,
                            onTitleClick = themeCycleAction,
                            paddingValues = paddingValues
                        )
                    }
                }
            }
        }
    }
}
