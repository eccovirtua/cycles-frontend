package com.example.cycles


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.cycles.navigation.AppNavHost
import com.example.cycles.navigation.Screen
import com.example.cycles.ui.components.BottomNavBar
import com.example.cycles.ui.theme.AnimatedBackground
import com.example.cycles.ui.theme.CyclesTheme
import com.example.cycles.ui.theme.ThemeColors
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.ui.graphics.Color

val currentThemeIndex = mutableIntStateOf(0)

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val activeColorScheme = ThemeColors[currentThemeIndex.intValue]
            val themeCycleAction = {
                currentThemeIndex.intValue = (currentThemeIndex.intValue + 1) % ThemeColors.size
            }

            CyclesTheme(activeColorScheme) {

                val navController = rememberNavController()

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val screensWithBottomBar = listOf(
                    Screen.Home.route,
                    Screen.InteractiveMusic.route,
                    Screen.InteractiveBooks.route,
                    Screen.InteractiveMovies.route,
                    "interactive/{domain}"
                )

                val shouldShowBottomBar = currentRoute in screensWithBottomBar

                AnimatedBackground(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                    ) {
                        Scaffold(
                            containerColor = Color.Transparent,
                            modifier = Modifier.fillMaxSize(),

                            contentWindowInsets = WindowInsets.navigationBars.only(WindowInsetsSides.Vertical),

                            bottomBar = {
                                if (shouldShowBottomBar) {
                                    BottomNavBar(
                                        navController = navController,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                }
                            }
                        ) { paddingValues ->
                            // Pasamos el padding completo
                            AppNavHost(
                                navController = navController,
                                onTitleClick = themeCycleAction,
                                paddingValues = paddingValues,
                            )
                        }
                    }
                }
            }
        }
    }
}
