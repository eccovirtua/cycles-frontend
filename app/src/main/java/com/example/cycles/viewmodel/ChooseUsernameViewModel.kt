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

    private val emailArg: String? = savedStateHandle["email"]
    private val passwordArg: String? = savedStateHandle["password"]
    private val ageArg: Int? = savedStateHandle["age"]
    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()
    private val _isAvailable = MutableStateFlow<Boolean?>(null) // null=sin verificar, true=libre, false=ocupado
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    fun onNameChange(newName: String) {
        _name.value = newName
        _isAvailable.value = null
        _error.value = null
    }

    fun checkUsernameAndRegister(navController: NavController) {
        val username = _name.value.trim()

        if (emailArg == null || passwordArg == null || ageArg == null) {
            _error.value = "Error crítico: Faltan datos del registro. Reinicia la app."
            return
        }

        if (username.length < 4) {
            _error.value = "El nombre de usuario es muy corto"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            // Verificar disponibilidad de Username en Backend
            val isUserFree = repository.checkUsernameAvailability(username)
            if (!isUserFree) {
                _error.value = "El nombre de usuario ya está ocupado"
                _isLoading.value = false
                return@launch
            }

            // Crear en Firebase
            // Nota: Firebase tiene la última palabra
            repository.registerWithEmail(emailArg, passwordArg)
                .addOnSuccessListener { authResult ->
                    val uid = authResult.user?.uid
                    if (uid != null) {
                        // Paso C: Crear en Mongo
                        finalizeBackendRegistration(username, emailArg, ageArg, uid, navController)
                    }
                }
                .addOnFailureListener { e ->
                    _isLoading.value = false
                    if (e.message?.contains("email", ignoreCase = true) == true) {
                        _error.value = "El correo fue tomado recientemente."
                    } else {
                        _error.value = "Error Firebase: ${e.message}"
                    }
                }
        }
    }

    private fun finalizeBackendRegistration(username: String, email: String, age: Int, uid: String, nav: NavController) {
        viewModelScope.launch {
            val request = UserCreateRequest(username, age, email, uid)
            val success = repository.createUserBackend(request)

            if (success) {
                _isLoading.value = false
                nav.navigate("home_screen") { popUpTo("auth_graph") { inclusive = true } }
            } else {
                // Borrar de Firebase si falla Mongo
                FirebaseAuth.getInstance().currentUser?.delete()
                _isLoading.value = false
                _error.value = "Error al guardar perfil. Intenta de nuevo."
            }
        }
    }
}