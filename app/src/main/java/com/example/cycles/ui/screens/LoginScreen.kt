package com.example.cycles.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.cycles.R
import com.example.cycles.navigation.Screen
import com.example.cycles.ui.components.CyclesPrimaryButton
import com.example.cycles.viewmodel.LoginViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Estados del ViewModel
    val isLoading by viewModel.isLoading
    val loginError by viewModel.loginError
    val isSuccess by viewModel.isLoginSuccess

    // Estados Locales para el formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. Configuración del Cliente de Google
    // IMPORTANTE: default_web_client_id se genera automáticamente al procesar google-services.json
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // 2. Lanzador para el resultado de Google (Activity Result)
    val googleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // Suprimimos la advertencia porque sabemos lo que hacemos
        @Suppress("DEPRECATION")
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { token ->
                // ÉXITO: Pasamos el token al ViewModel
                viewModel.firebaseAuthWithGoogle(token)
            }
        } catch (e: ApiException) {
            Log.e("LoginScreen", "Google Sign In falló código: ${e.statusCode}")
        }
    }

    // 3. Efecto de navegación al éxito
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // 4. Efecto para mostrar errores en Snackbar
    LaunchedEffect(loginError) {
        loginError?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    // UI Principal con Scaffold
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 25.dp),
                verticalArrangement = Arrangement.Center, // Centrado verticalmente
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(Modifier.height(30.dp))

                // Campo Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    shape = RoundedCornerShape(6.dp),
                    placeholder = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Filled.AlternateEmail, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(16.dp))

                // Campo Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    shape = RoundedCornerShape(6.dp),
                    placeholder = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )

                Spacer(Modifier.height(24.dp))

                // Botón Login con Email
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    CyclesPrimaryButton(
                        text = "Entrar con Email",
                        enabled = email.isNotEmpty() && password.isNotEmpty(),
                        onClick = { viewModel.login(email, password) },
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(text = "— O —", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(16.dp))

                // Botón Login con Google
                OutlinedButton(
                    onClick = {
                        googleLauncher.launch(googleSignInClient.signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    // Puedes poner un Icono de Google aquí si tienes el recurso
                    Text("Iniciar sesión con Google")
                }

                Spacer(Modifier.height(24.dp))

                // Link Registro
                TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                    Text("¿No tienes cuenta? Regístrate")
                }

                // Link Olvidé contraseña
                TextButton(onClick = { navController.navigate(Screen.ForgotPassword.route) }) {
                    Text("¿Olvidaste tu contraseña?")
                }
            }
        }
    }
}