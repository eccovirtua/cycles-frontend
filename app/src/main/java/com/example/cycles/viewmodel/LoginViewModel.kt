package com.example.cycles.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.GoogleAuthProvider


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado simple para la UI
    var isLoading = mutableStateOf(false)
    var loginError = mutableStateOf<String?>(null)
    var isLoginSuccess = mutableStateOf(false)

    // Login clásico
    fun login(email: String, pass: String) {
        isLoading.value = true
        loginError.value = null

        authRepository.loginWithEmail(email, pass)
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
        // Convertimos el token de Google en credencial de Firebase
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