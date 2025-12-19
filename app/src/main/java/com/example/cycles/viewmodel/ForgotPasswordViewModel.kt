package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cycles.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun onEmailChanged(value: String) {
        _email.value = value
    }

    fun clearMessage() {
        _message.value = null
    }

    fun sendRecoveryEmail() {
        if (email.value.isBlank()) {
            _message.value = "Ingresa tu correo"
            return
        }

        _isLoading.value = true
        authRepository.sendPasswordResetEmail(email.value)
            .addOnSuccessListener {
                _message.value = "Correo de recuperación enviado. Revisa tu bandeja."
            }
            .addOnFailureListener { e ->
                _message.value = "Error: ${e.localizedMessage}"
            }
            .addOnCompleteListener {
                _isLoading.value = false
            }
    }
}
