package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth = _dateOfBirth.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isRegisterSuccess = MutableStateFlow(false)

    // 2. VARIABLE PÚBLICA (Inmutable): La UI solo puede leerla (observarla)
    val isRegisterSuccess = _isRegisterSuccess.asStateFlow()

    private val _navigateToNextStep = MutableStateFlow(false)
    val navigateToNextStep = _navigateToNextStep.asStateFlow()

    private var _validatedAge: Int = 0

    // --- Actualizaciones de estado ---
    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPass: String) { _password.value = newPass }

    fun updateDateOfBirth(newDate: String) {
        _dateOfBirth.value = newDate
    }

    // --- Cálculo de edad ---
    private fun calculateAgeFromString(dateString: String): Int? {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dateString, formatter)
            Period.between(birthDate, LocalDate.now()).years
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAgeForNavigation(): Int {
        return _validatedAge
    }

    // --- Lógica del botón Registrar ---
    fun onRegisterClick() {
        val currentEmail = _email.value
        val currentPass = _password.value
        val currentDobString = _dateOfBirth.value

        if (currentEmail.isBlank() || currentPass.isBlank() || currentDobString.isBlank()) {
            _error.value = "Por favor completa todos los campos"
            return
        }

        val age = calculateAgeFromString(currentDobString)
        if (age == null) {
            _error.value = "Formato de fecha inválido"
            return
        }


        if (age < 14) {
            _error.value = "Debes ser mayor de 14 años para registrarte ($age años)"
            return
        }

        // Validacion remota
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            if (isEmailFree) {
                // si está disponible el correo:

                // Guardar edad antes de registrar
                _validatedAge = age
                _navigateToNextStep.value = true
            } else {
                _error.value = "Este Email ya está en uso."
            }
            _isLoading.value = false
        }



        register(currentEmail, currentPass)
    }

    fun register(email: String, password: String) {
        _isLoading.value =  true
        // 1. registrar en firebase
        repository.registerWithEmail(email, password)
            .addOnSuccessListener { authResult ->
                val newUid = authResult.user?.uid
                if (newUid != null) {
                    _isLoading.value = false
                    _isRegisterSuccess.value = true
                }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _error.value = "Error firebase: ${e.message}"
            }
    }
}