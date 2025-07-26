package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.RegisterRequest
import com.example.cycles.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository   //inyectamos el repo aquí
) : ViewModel() {

    private val _email       = MutableStateFlow("")
    val email               = _email.asStateFlow()

    private val _password    = MutableStateFlow("")
    val password            = _password.asStateFlow()

    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth: StateFlow<String> = _dateOfBirth.asStateFlow()

    private val _userAge     = MutableStateFlow<Int?>(null)
    val userAge: StateFlow<Int?> = _userAge.asStateFlow()  // ← exponer userAge

    private val _isLoading   = MutableStateFlow(false)
    val isLoading           = _isLoading.asStateFlow()

    private val _error       = MutableStateFlow("")
    val error               = _error.asStateFlow()

    private val _uiEvent     = MutableSharedFlow<Unit>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _jwtToken = MutableStateFlow<String?>(null)
    val jwtToken = _jwtToken.asStateFlow()


    // --- Actualizaciones de estado ---
    fun onEmailChange(new: String) { _email.value = new; _error.value = "" }
    fun onPasswordChange(new: String) { _password.value = new; _error.value = "" }
    fun updateDateOfBirth(newDate: String) {
        _dateOfBirth.value = newDate
        _error.value = ""
        calculateAge(newDate)
    }

    // --- Cálculo de edad ---
    private fun calculateAge(dob: String) {
        if (dob.isBlank()) {
            _userAge.value = null
            return
        }
        try {
            val fmt       = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dob, fmt)
            val today     = LocalDate.now()
            _userAge.value = Period.between(birthDate, today).years
        } catch (_: Exception) {
            _userAge.value = null
            _error.value   = "Formato de fecha inválido"
        }
    }

    // --- Validaciones ---
    private fun isValidEmail(email: String) =
        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()

    private fun isValidPassword(pw: String) =
        pw.length in 10..25

    private fun isValidDateOfBirth(dob: String): Boolean {
        if (dob.isBlank()) return false
        return try {
            val fmt       = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dob, fmt)
            val today     = LocalDate.now()
            !birthDate.isAfter(today) && Period.between(birthDate, today).years > 9
        } catch (_: DateTimeParseException) {
            false
        } catch (_: Exception) {
            false
        }
    }

    // --- Lógica del botón Registrar ---
    fun onRegisterClick() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value     = ""

            // 1) Campos obligatorios
            if (email.value.isBlank() || password.value.isBlank() || dateOfBirth.value.isBlank()) {
                _error.value     = "Todos los campos son obligatorios"
                _isLoading.value = false
                return@launch
            }
            // 2) Formato email
            if (!isValidEmail(email.value)) {
                _error.value     = "Correo inválido"
                _isLoading.value = false
                return@launch
            }
            // 3) Longitud contraseña
            if (!isValidPassword(password.value)) {
                _error.value     = "Contraseña debe tener 10–25 caracteres"
                _isLoading.value = false
                return@launch
            }
            // 4) Fecha y edad mínima
            if (!isValidDateOfBirth(dateOfBirth.value)) {
                _error.value     = "Debes ser mayor de 9 años para continuar"
                _isLoading.value = false
                return@launch
            }

            // 5) Llamada real al repositorio dentro del try/catch
            try {
                val age = userAge.value
                    ?: throw IllegalArgumentException("Edad inválida")
                val request = RegisterRequest(
                    name     = "",
                    email    = email.value,
                    age      = age,
                    password = password.value
                )
                val response = repository.register(request)
                _jwtToken.value = response.jwtToken
                _uiEvent.emit(Unit)
            } catch (e: Exception) {
                _error.value = "Error al registrar: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
