package com.example.cycles.ui.screens


//imports para el preview
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.viewmodel.WelcomeViewModel
import androidx.compose.ui.unit.sp // Importa sp para el tamaño de fuente
import com.example.cycles.navigation.Screen
import com.example.cycles.ui.theme.HelveticaFamily

@Composable
        fun WelcomeScreen(
            navController: NavHostController,
            paddingValues: PaddingValues,
            welcomeViewModel: WelcomeViewModel = hiltViewModel(),
            onTitleClick: () -> Unit
        ) {

            val message by welcomeViewModel.welcomeMessage.collectAsState()

            // UI principal
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(400.dp)) //espacio hacia abajo

                    // Mensaje dinámico con gradiente
                    Text(
                        text = message,
                        style = TextStyle(
                            fontFamily = HelveticaFamily,
                            color = Color.White,
                            fontSize = 46.sp,
                            fontWeight = FontWeight.Normal
                        ).copy(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Red, Color.Magenta, Color.Red)
                            )
                        ),
                        modifier = Modifier.offset(y = (-80).dp).clickable {
                            onTitleClick()
                        }

                    )
                    Spacer(modifier = Modifier.height(10.dp)) //mientras mas alto mas abajo los botones
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(
                            onClick = { navController.navigate(Screen.Login.route) },
                            shape = RoundedCornerShape(25),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Iniciar sesión",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Button(
                            onClick = { navController.navigate(Screen.Register.route) },
                            shape = RoundedCornerShape(25),

                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Registrarse",
                                fontSize = 18.sp
                            )

                        }
                    }

                }
            }
        }