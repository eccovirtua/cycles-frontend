    package com.example.cycles.ui.screens


        //imports para el preview
        import androidx.compose.ui.graphics.Brush
        import androidx.compose.foundation.Image
        import androidx.compose.foundation.clickable
        import androidx.compose.foundation.shape.RoundedCornerShape
        import androidx.compose.foundation.layout.*
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.graphics.Color
        import androidx.compose.ui.res.painterResource
        import androidx.compose.ui.text.TextStyle
        import androidx.compose.ui.text.font.FontWeight
        import androidx.compose.ui.unit.dp
        import androidx.hilt.navigation.compose.hiltViewModel
        import androidx.navigation.NavHostController
        import com.example.cycles.R
        import com.example.cycles.viewmodel.WelcomeViewModel
        import androidx.compose.ui.unit.sp // Importa sp para el tamaño de fuente
        import com.example.cycles.navigation.Screen

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
                    // FÓRMULA DE COMPENSACIÓN: Spacer A + Spacer C debe ser igual a 340.dp

                    // SPACER A: Controla el margen superior del Logo/Texto.
                    // Aumentar este valor mueve el Logo y el Texto hacia abajo.
                    Spacer(modifier = Modifier.height(280.dp)) //espacio hacia abajo
                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.logociclos),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(150.dp)

                    )
                    // SPACER B: Espacio fijo entre Logo y Texto (mantener en 90.dp)
                    Spacer(modifier = Modifier.height(90.dp))

                    // Mensaje dinámico con gradiente
                    Text(
                        text = message,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold
                        ).copy(
                            brush = Brush.linearGradient(
                                colors = listOf(Color.Red, Color.Magenta, Color.Red)
                            )
                        ),
                        modifier = Modifier.offset(y = (-80).dp).clickable {
                            onTitleClick()
                        }

                    )


                    // SPACER C: Controla la posición de los botones.
                    // DEBE ser 340.dp - Spacer A. (Ej: 340 - 175 = 165)
                    // Si quieres que el Logo/Texto bajen, AUMENTA A y REDUCE C.
                    Spacer(modifier = Modifier.height(115.dp)) //mientras mas alto mas abajo los botones
                    // Botones
                    //Button	Elevado, fondo sólido, sombra sutil.	Acciones primarias y de alta importancia.
        //            FilledTonalButton	Fondo sólido más claro (tono), sin elevación.	Acciones secundarias en contenedores de color.
        //            ElevatedButton	Similar a Button, pero el énfasis es en la elevación/sombra.	Romper un poco con fondos sólidos para atraer la atención.
        //                    OutlinedButton	Borde delgado, fondo transparente.	Acciones secundarias que no deben dominar la UI.
        //            TextButton	Solo texto, fondo transparente, sin borde ni elevación.	Acciones de baja prioridad o dentro de diálogos.
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