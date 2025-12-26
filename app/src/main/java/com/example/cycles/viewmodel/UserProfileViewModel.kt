package com.example.cycles.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Estado de la UI (Sin Bio)
data class UserProfileState(
    val isLoading: Boolean = false,
    val username: String = "",        // Mapeado desde 'name'
    val age: Int = 0,                 // Mapeado desde 'age'
    val country: String = "",         // Puede venir null
    val profileImageUrl: String? = null, // Viene del backend
    val coverImageUrl: String? = null,   // Puede venir null
    val error: String? = null
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> = _isLoggedOut.asStateFlow()

    fun loadUserProfileData() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            _state.update { it.copy(error = "Usuario no autenticado") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Llamada única al Backend (FastAPI -> MongoDB)
            val result = userRepository.getUserProfile(currentUser.uid)

            result.onSuccess { userDto ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        // Mapeo de datos:
                        username = "@${userDto.name}", // Agregamos el @ visualmente
                        age = userDto.age,
                        // La foto YA viene en el JSON de Mongo, la usamos directo:
                        profileImageUrl = userDto.profilePictureUrl,
                        // Manejo de opcionales:
                        // Si coverImageUrl es null en el JSON, se queda null en el estado
                        coverImageUrl = userDto.coverImageUrl,
                        // Si country es null, ponemos un texto por defecto o vacío
                        country = userDto.country ?: ""
                    )
                }
            }.onFailure { exception ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar perfil: ${exception.message}"
                    )
                }
            }
        }
    }

    fun performLogout() {
        auth.signOut()
        _isLoggedOut.value = true
    }
}