package com.example.cycles.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.viewmodel.WelcomeViewModel
import androidx.compose.ui.unit.sp
import com.example.cycles.R
import com.example.cycles.navigation.Screen
import com.example.cycles.ui.components.LanguageSelector
import com.example.cycles.ui.components.ScrollingText
import com.example.cycles.ui.theme.HelveticaFamily
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun WelcomeScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    welcomeViewModel: WelcomeViewModel = hiltViewModel(),
    onTitleClick: () -> Unit
) {
    val context = LocalContext.current

    // --- ESTADOS ---
    // 1. Conectamos los inputs al ViewModel (Fuente de verdad)
    val email by welcomeViewModel.email.collectAsState()
    val password by welcomeViewModel.password.collectAsState()

    val message by welcomeViewModel.welcomeMessage.collectAsState()
    val isSuccess by welcomeViewModel.isLoginSuccess
    val isLoading by welcomeViewModel.isLoading
    val errorMsg by welcomeViewModel.loginError

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- CONFIGURACIÓN GOOGLE ---
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        @Suppress("DEPRECATION")
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                welcomeViewModel.firebaseAuthWithGoogle(token)
            }
        } catch (e: ApiException) {
            Log.e("WelcomeScreen", "Google Sign In falló código: ${e.statusCode}")
        }
    }

    // --- EFECTOS ---
    // Navegación al éxito
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate(Screen.Home.route) { // Asumo que Dashboard es Screen.Dashboard, ajusta si es Home
                popUpTo(Screen.Welcome.route) { inclusive = true }
            }
        }
    }
    // Mostrar errores en Snackbar
    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    // --- UI PRINCIPAL ---
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 30.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.height(200.dp))

            // 1. Texto Recommender
            Text(
                text = message,
                style = TextStyle(
                    fontFamily = HelveticaFamily,
                    color = Color.White,
                    fontSize = 43.sp,
                    fontWeight = FontWeight.Bold
                ).copy(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Red, Color.Magenta, Color.Red)
                    )
                ),
                modifier = Modifier
                    .offset(y = (-10).dp)
                    .clickable { onTitleClick() }
                    .padding(horizontal = 1.dp)
            )

            // 2. Animación de texto
            ScrollingText()

            Spacer(modifier = Modifier.height(40.dp))

            // 3. CAMPO EMAIL
            OutlinedTextField(
                value = email,
                onValueChange = { welcomeViewModel.onEmailChange(it) },
                shape = RoundedCornerShape(35),
                placeholder = { Text(stringResource(R.string.field_email)) },
                leadingIcon = { Icon(Icons.Filled.AlternateEmail, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.height(16.dp))

            // 4. CAMPO PASSWORD
            OutlinedTextField(
                value = password,
                onValueChange = { welcomeViewModel.onPasswordChange(it) },
                shape = RoundedCornerShape(35),
                placeholder = { Text(stringResource(R.string.field_password)) },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Spacer(Modifier.height(26.dp))

            // 5. BOTÓN LOGIN (EMAIL)
            FilledTonalButton(
                enabled = !isLoading,
                onClick = { welcomeViewModel.login() },
                shape = RoundedCornerShape(35),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = stringResource(R.string.home_login),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } // <--- CIERRA EL BOTÓN AQUÍ

            // AHORA LO SIGUIENTE ESTÁ FUERA DEL BOTÓN Y SE VERÁ EN LA COLUMNA PRINCIPAL

            Spacer(Modifier.height(10.dp))

            // Separador visual
            Text(
                text = "————————————————",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(10.dp))

            // 6. BOTÓN LOGIN (GOOGLE)
            FilledTonalButton(
                onClick = {
                    googleLauncher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(35),
                enabled = !isLoading
            ) {
                Image(
                    painter = painterResource(id = R.drawable.android_light_rd_na),
                    contentDescription = "Logo de Google",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = stringResource(R.string.home_google),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(Modifier.height(34.dp))

            // 7. TextButtons (Registro)
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text(stringResource(R.string.home_register))
            }

            Text(
                text = "—————————",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(2.dp))

            TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                Text(stringResource(R.string.home_forgotpassword))
            }

            Spacer(Modifier.height(30.dp))

            // 8. Componente idioma
            LanguageSelector()

            // Espacio final para que el scroll llegue bien abajo
            Spacer(Modifier.height(50.dp))

        } // Cierre Column
    } // Cierre Box
} // Cierre WelcomeScreen