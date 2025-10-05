package com.example.cycles


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableIntStateOf
import androidx.navigation.compose.rememberNavController
import com.example.cycles.navigation.AppNavHost
import com.example.cycles.ui.theme.CyclesTheme
import com.example.cycles.ui.theme.ThemeColors
import dagger.hilt.android.AndroidEntryPoint





// 1. ESTADO GLOBAL: El índice del tema actual (debe estar fuera de la clase)
val currentThemeIndex = mutableIntStateOf(0)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // 2. OBTENER ESQUEMA: Accede al esquema de color activo de la lista ThemeColors
            val activeColorScheme = ThemeColors[currentThemeIndex.intValue]

            // 🎯 3. DEFINIR LA ACCIÓN DE CLIC: Función lambda que cambia el estado
            val themeCycleAction = {
                // Avanza al siguiente índice y vuelve al inicio (0) si llega al final
                currentThemeIndex.intValue = (currentThemeIndex.intValue + 1) % ThemeColors.size
            }

            // Aplica el tema global, pasando el esquema activo
            CyclesTheme(activeColorScheme) {

                // Crea y recuerda un navHostController
                val navController = rememberNavController()

                // Llama al host de navegación, pasando el controlador y la acción de clic
                AppNavHost(
                    navController = navController,
                    onTitleClick = themeCycleAction // 🎯 CORRECCIÓN: Ahora 'themeCycleAction' existe
                )
            }
        }
    }
}
