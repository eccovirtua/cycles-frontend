package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.GoogleAuthProvider
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
    private val _navigateToNextStep = MutableStateFlow(false)
    val navigateToNextStep = _navigateToNextStep.asStateFlow()

    private val _navigateToHome = MutableStateFlow(false)
    val navigateToHome = _navigateToHome.asStateFlow()

    private var _validatedAge: Int = 0
    private val _navigateToNextStepGoogle = MutableStateFlow(false)
    val navigateToNextStepGoogle = _navigateToNextStepGoogle.asStateFlow()

    var googleEmail: String? = null

    // --- Actualizaciones de estado ---
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPass: String) {
        _password.value = newPass
    }

    fun updateDateOfBirth(newDate: String) {
        _dateOfBirth.value = newDate
    }
    fun onGoogleSignInResult(idToken: String) {
        _isLoading.value = true
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        repository.signInWithGoogle(credential)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                googleEmail = user?.email
                _isLoading.value = false
                verificarSiExisteEnMongo()
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _error.value = "Error Google: ${e.message}"
            }
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

            val isEmailFree = repository.checkEmailAvailability(currentEmail)

            if (isEmailFree) {
                // si está disponible el correo:
                // Guardar edad antes de registrar
                _validatedAge = age

                // Proceder a pantalla de elegir username
                _navigateToNextStep.value = true
            } else {
                _error.value = "Este Email ya está en uso."
            }
            _isLoading.value = false
        }
    }

    private fun verificarSiExisteEnMongo() {
        viewModelScope.launch {
            // Delay de seguridad para el Token (igual que en WelcomeViewModel)
            kotlinx.coroutines.delay(400)

            val usuarioExiste = repository.checkUserExists()

            _isLoading.value = false

            if (usuarioExiste) {
                // CASO A: El usuario se equivocó y usó una cuenta ya registrada -> LO MANDAMOS AL HOME
                _navigateToHome.value = true
            } else {
                // CASO B: Usuario realmente nuevo -> SEGUIMOS EL REGISTRO
                _navigateToNextStepGoogle.value = true
            }
        }
    }
}