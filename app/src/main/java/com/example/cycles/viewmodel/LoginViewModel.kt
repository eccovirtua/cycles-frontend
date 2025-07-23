package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.AuthenticationRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow // Needed for _uiEvent
import kotlinx.coroutines.flow.asSharedFlow // Needed for _uiEvent
import javax.inject.Inject
import com.example.cycles.repository.AuthRepository


@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: AuthRepository): ViewModel() {
    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    // Estado expuesto al Composable (solo lectura)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

   //shrared flow para eventos UI
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    fun onEmailChange(new: String) { _email.value = new }
    fun onPasswordChange(new: String) { _password.value = new}

    fun onLoginClick() {
        viewModelScope.launch {
            // Set isLoading to true at the start of the operation
            _isLoading.value = true
            _error.value = "" // limpiar errores previos

            try {
                // hacer login
                val response = repository.login(
                    AuthenticationRequest(
                        email    = email.value,
                        password = password.value
                    )
                )
                // si el login es correcto:
                _uiEvent.emit("Login exitoso, token: ${response.jwtToken}")

                // limpiar valores de los campos despues de un login exitoso
                _email.value = ""
                _password.value = ""



            } catch (e: Exception) {

                _error.value = "Error al autenticar: ${e.message}"

                _uiEvent.emit("Error: ${e.message}")
            } finally {
                // Ensure isLoading is set to false regardless of success or failure
                _isLoading.value = false
            }
        }
    }
}

