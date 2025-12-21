package com.example.cycles.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // --- ESTADOS DE DATOS (Fuente de la verdad) ---
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _welcomeMessage = MutableStateFlow("Recommender™")
    val welcomeMessage: StateFlow<String> = _welcomeMessage

    // --- ESTADOS DE UI (Carga y Errores) ---
    var isLoading = mutableStateOf(false)
    var loginError = mutableStateOf<String?>(null)
    var isLoginSuccess = mutableStateOf(false)

    // --- SETTERS (La UI llama a esto cuando el usuario escribe) ---
    fun onEmailChange(newValue: String) {
        _email.value = newValue
    }

    fun onPasswordChange(newValue: String) {
        _password.value = newValue
    }


    // Login Clásico
    fun login() {
        val rawInput = _email.value
        val passwordInput = _password.value

        // Validación básica antes de llamar a Firebase
        if (rawInput.isBlank() || passwordInput.isBlank()) {
            loginError.value = "Por favor completa todos los campos"
            return
        }

        isLoading.value = true
        loginError.value = null

        // Caso 1: la input de email contiene un @ por lo que es mail, no se debe consultar con nada adicionalmente
        if (rawInput.contains("@")) {
            performFirebaseLogin(rawInput, passwordInput)
            return
        }

        // Caso 2: la input del email no contiene un @ por lo que es un nombre de usuario, consultar con la API
        viewModelScope.launch {
            try {
                // buscar email en base de datos (atlas)
                val realEmail = authRepository.getEmailFromUsername(rawInput)
                if (realEmail != null) {
                    // se encontró el email correctamente, se procede a usar ese email para consultar con firebase
                    performFirebaseLogin(realEmail, passwordInput)
                } else {
                    isLoading.value = false
                    loginError.value = "Usuario no encontrado"
                }
            } catch (e: Exception) {
                isLoading.value = false
                loginError.value = e.localizedMessage ?: "Error de conexion con el servidor"
            }
        }

        authRepository.loginWithEmail(rawInput, passwordInput)
            .addOnSuccessListener {
                isLoading.value = false
                isLoginSuccess.value = true
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                loginError.value = e.localizedMessage ?: "Error al iniciar sesión"
            }
    }
    private fun performFirebaseLogin(email: String, pass: String) {
        authRepository.loginWithEmail(email, pass)
            .addOnSuccessListener {
                isLoading.value = false
                isLoginSuccess.value = true
            }
            .addOnFailureListener { _ ->
                isLoading.value = false
                loginError.value = "Usuario o contraseña incorrectos."
            }



    }

    // Login con Google
    fun firebaseAuthWithGoogle(idToken: String) {
        isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        authRepository.signInWithGoogle(credential)
            .addOnSuccessListener {
                isLoading.value = false
                isLoginSuccess.value = true
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                loginError.value = "Error Google: ${e.message}"
            }
    }
}