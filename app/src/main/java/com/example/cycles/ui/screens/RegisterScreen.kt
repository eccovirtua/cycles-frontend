package com.example.cycles.ui.screens

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.cycles.R
import com.example.cycles.navigation.Screen
import com.example.cycles.ui.theme.HelveticaFamily
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import androidx.compose.material.icons.filled.Email
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.example.cycles.viewmodel.RegisterViewModel
import com.example.cycles.ui.components.DateOfBirthPicker
import com.google.android.gms.common.api.ApiException


@Composable
fun RegisterScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val navigateToNextStep by viewModel.navigateToNextStep.collectAsState()
    val navigateToNextStepGoogle by viewModel.navigateToNextStepGoogle.collectAsState() // <--- NUEVO
    // Estados
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMsg by viewModel.error.collectAsState()

    val context = LocalContext.current
    val dob by viewModel.dateOfBirth.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    // CONFIGURACIÓN DE GOOGLE SIGN IN CLIENT
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id)) // Tu ID de cliente web de Firebase
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
                viewModel.onGoogleSignInResult(token)
            }
        } catch (e: ApiException) {
            Log.e("WelcomeScreen", "Google Sign In falló código: ${e.statusCode}")
        }
    }

    LaunchedEffect(navigateToNextStepGoogle) {
        if (navigateToNextStepGoogle) {
            navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
                handle["email"] = viewModel.googleEmail // El email que obtuvimos de Google
                handle["password"] = null // NULL indica flujo Google
                handle["age"] = null      // NULL indica que ChooseUsername debe pedir la edad
            }
            navController.navigate(Screen.ChooseUsername.route)
        }
    }


    LaunchedEffect(navigateToNextStep) {
        if (navigateToNextStep) {
            // Pasamos la "mochila" de datos al siguiente fragmento de navegación
            navController.currentBackStackEntry?.savedStateHandle?.let { handle ->
                handle["email"] = viewModel.email.value
                handle["password"] = viewModel.password.value
                handle["age"] = viewModel.getAgeForNavigation()
            }
            navController.navigate(Screen.ChooseUsername.route)
        }
    }



    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 25.dp, vertical = 29.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(40.dp))
            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontFamily = HelveticaFamily
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(20.dp)) //separacion de titulo y subtitulo
            Text(
                text = stringResource(R.string.register_subtitle),
                fontFamily = HelveticaFamily,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 1.dp),
            )
            Spacer(Modifier.height(10.dp))
            // TEXTO DE EMAIL ETIQUETA ARRIBA DEL CAMPO
            Text(
                text = stringResource(R.string.register_topfield),
                fontFamily = HelveticaFamily,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))

            // Campo de Correo
            OutlinedTextField(
                value = email,
                onValueChange = viewModel::onEmailChange,

                shape = RoundedCornerShape(35),
                placeholder = {
                    Text(
                        stringResource(R.string.register_emailfield),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = HelveticaFamily
                    )
                },
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Icono de correo")
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.fillMaxWidth(), //altura del boton
            )
            Spacer(Modifier.height(12.dp)) //espacio entre correo y password

            // Etiqueta "Contraseña"
            Text(
                text = stringResource(R.string.register_topfieldpw),
                fontFamily = HelveticaFamily,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))

            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChange,

                shape = RoundedCornerShape(35),
                placeholder = {
                    Text(
                        stringResource(R.string.register_pwfield),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = HelveticaFamily
                    )
                },

                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = "Icono de contraseña")
                },

                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),

                modifier = Modifier.fillMaxWidth(), //altura del boton
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.register_dobtop),
                fontFamily = HelveticaFamily,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(2.dp))

            // DateOfBirthPicker
            DateOfBirthPicker(
                selectedDate = dob,
                onDateSelected = { newDateString ->
                    focusManager.clearFocus()
                    viewModel.updateDateOfBirth(newDateString)
                },
                modifier = Modifier.fillMaxWidth(),

                )
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.register_tos),
                fontFamily = HelveticaFamily,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(18.dp))


            // Mensaje de Error
            if (!errorMsg.isNullOrEmpty()) {
                Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
            }

            // Botón Registrarse
            Button(
                onClick = viewModel::onRegisterClick,
                shape = RoundedCornerShape(35),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() && dob.isNotEmpty()
            ) {
                if (isLoading) CircularProgressIndicator(modifier = Modifier.size(20.dp))
                else Text(stringResource(R.string.register_next), fontFamily = HelveticaFamily)
            }
            Spacer(Modifier.height(16.dp))

            // Botón Iniciar Sesión
            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text(stringResource(R.string.register_txtbtn), fontFamily = HelveticaFamily)
            }
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
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(paddingValues)
            )
        }
    }
}