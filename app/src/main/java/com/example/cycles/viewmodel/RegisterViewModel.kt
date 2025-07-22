package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow



@HiltViewModel
class RegisterViewModel @Inject constructor(): ViewModel() {

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    // Estado expuesto al Composable (solo lectura boolean)
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth: StateFlow<String> = _dateOfBirth.asStateFlow()

    //sharedflow para eventos de UI
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()


    // Opcional: Un estado para exponer la edad directamente a la UI si la necesitas mostrar
    private val _userAge = MutableStateFlow<Int?>(null)



    //actualizaciones
    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
        _error.value = "" // Limpiar error al cambiar texto
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        _error.value = "" // Limpiar error al cambiar texto
    }

    fun updateDateOfBirth(newDate: String) {
        _dateOfBirth.value = newDate
        _error.value = ""
        calculateAge(newDate)

    }


    //función para calcular la edad:
    private fun calculateAge(dobString: String) {
        if (dobString.isNotEmpty()) {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val birthDate = LocalDate.parse(dobString, formatter)
                val currentDate = LocalDate.now()

                val period = Period.between(birthDate, currentDate)
                val age = period.years
                _userAge.value = age // Actualiza el estado de la edad
                println("Edad calculada: $age años") // Para depuración
            } catch (e: Exception) {
                // Manejar error si la fecha no es válida o no se puede parsear
                _userAge.value = null
                _error.value = "Formato de fecha de nacimiento inválido."
                println("Error al calcular edad: ${e.message}")
            }
        } else {
            _userAge.value = null
        }
    }

    //validaciones (email, contraseña y edad)
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        // Regex para validar un email. Es una validación básica, no exhaustiva.
        // Requiere al menos un carácter antes de @, el @, al menos un carácter después de @,
        // un punto y al menos dos caracteres después del punto.
    }

    /**
     * Valida si la contraseña cumple con los requisitos de longitud.
     */
    private fun isValidPassword(password: String): Boolean {
        return password.length in 10..25
    }

    private fun isValidDateOfBirth(dobString: String): Boolean {
        if (dobString.isBlank()) { //validación extra (creo que ya hay una)
            return false
        }
        try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dobString, formatter)
            val currentDate = LocalDate.now()
            //validación de que la fecha de nacimiento no sea en el futuro XD
            if (birthDate.isAfter(currentDate)) {
                return false
            }
            //calcular edad y verificar si es mayor a 9 años
            val period = Period.between(birthDate, currentDate)
            return period.years > 9
        } catch (e: DateTimeParseException) {
            return false //la fecha no tiene el formato esperado
        } catch (e: Exception) {
            return false //cualquier otro error
        }
    }


    fun onRegisterClick() { //logica del boton de registro con las validaciones
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = ""

            // 1. validar que los campos que no estén vacios
            if (email.value.isBlank() || password.value.isBlank() || dateOfBirth.value.isBlank()) {
                _error.value = "Todos los campos son obligatorios."
                _isLoading.value = false
                return@launch
            }

            //2. validar el correcto formato del correo
            if (!isValidEmail(email.value)) {
                _error.value = "Ingresa una dirección de correo válida."
                _isLoading.value = false
                return@launch
            }

            //3. validar la longitud de la contrseña
            if (!isValidPassword(password.value)) {
                _error.value = "La contraseña debe estar entre 10 y 25 carácteres"
                _isLoading.value = false
                return@launch
            }

            //4. validar la fecha de nacimiento y la edad mínima
            if (!isValidDateOfBirth(dateOfBirth.value)) {
                try {
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    val birthDate = LocalDate.parse(dateOfBirth.value, formatter)
                    val currentDate = LocalDate.now()
                    if (birthDate.isAfter(currentDate)) {
                        _error.value = "La fecha de nacimiento no puede ser en el futuro"
                    } else {
                        _error.value = "Debes tener más de 9 años para registrarte"
                    }
                } catch (e: DateTimeParseException) {
                    _error.value = "Formato de fecha de nacimiento inválido (DD/MM/AAAA)"
                } catch (e: Exception) {
                    _error.value = "Fecha de nacimiento inválida"
                }
                _isLoading.value = false
                return@launch
            }

            try { //si las validaciones pasan, procede con la logica de registro SIMULADA
                kotlinx.coroutines.delay(1500) //simular un delay de red, conexion
                val isSuccess=true //siempre será exitoso para ambiente local

                if (isSuccess) {
                    _uiEvent.emit("Todos los campos fueron validados correctamente, todo en orden listo para pasar al registro real")
                } else {
                    _error.value = "Error al registrar el usuario, inténtalo de nuevo."
                }


            } catch (e: Exception) {
                _error.value = "ocurrió un error inesperado durante el registro: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
