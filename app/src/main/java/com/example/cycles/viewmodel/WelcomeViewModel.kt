package com.example.cycles.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        val currentEmail = _email.value
        val currentPass = _password.value

        // Validación básica antes de llamar a Firebase
        if (currentEmail.isBlank() || currentPass.isBlank()) {
            loginError.value = "Por favor completa todos los campos"
            return
        }

        isLoading.value = true
        loginError.value = null

        authRepository.loginWithEmail(currentEmail, currentPass)
            .addOnSuccessListener {
                isLoading.value = false
                isLoginSuccess.value = true
            }
            .addOnFailureListener { e ->
                isLoading.value = false
                loginError.value = e.localizedMessage ?: "Error al iniciar sesión"
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