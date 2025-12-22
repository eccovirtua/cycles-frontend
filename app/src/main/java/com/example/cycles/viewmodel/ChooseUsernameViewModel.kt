package com.example.cycles.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserCreateRequest
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.navigation.NavController

@HiltViewModel
class ChooseUsernameViewModel @Inject constructor(
    private val repository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Recuperamos la edad automáticamente. "age" debe coincidir con la ruta del NavHost
    private val age: Int = checkNotNull(savedStateHandle["age"])

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private val _isAvailable = MutableStateFlow<Boolean?>(null) // null=sin verificar, true=libre, false=ocupado
    val isAvailable = _isAvailable.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
        _isAvailable.value = null
        _error.value = null
    }

    // Opcional: Función para verificar disponibilidad antes de guardar
    // (Requiere un endpoint GET /users/check/{username} en tu backend)
    fun checkAvailability() {
        val username = _name.value
        if (username.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            // Simulamos la verificación o llamamos a repository.checkUsername(username)
            // Por ahora, asumiremos que si el backend devuelve 409 al crear, es que no está disponible.
            // Si tienes el endpoint específico, úsalo aquí.
            _isLoading.value = false
            _isAvailable.value = true // Asumimos true para permitir el click en "Continuar"
        }
    }

    // Finalizar Registro
    fun saveUsername(navController: NavController) {
        val username = _name.value
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            _error.value = "Sesión perdida. Por favor inicia sesión nuevamente."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Preparamos el paquete completo para FastAPI
            val request = UserCreateRequest(
                username = username,
                email = currentUser.email ?: "", // Email de Firebase
                age = age,                       // Edad traída de la pantalla anterior
                firebaseUid = currentUser.uid    // UID de Firebase
            )
            try {
                // llama a @POST /users/create)
                val success = repository.createUserBackend(request)

                if (success) {
                    // EXITO: Usuario creado en Mongo y Firebase.

                    navController.navigate("home_screen") {
                        popUpTo("auth_graph") { inclusive = true }
                    }
                } else {
                    _error.value = "Error al crear perfil. Puede que el nombre de usuario ya exista."
                    _isAvailable.value = false
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}