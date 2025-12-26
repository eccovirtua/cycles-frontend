package com.example.cycles.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

// Estado de la UI (Sin Bio)
data class UserProfileState(
    val isLoading: Boolean = false,
    val username: String = "",
    val name: String = "",
    val profileImageUrl: String? = null,
    val coverImageUrl: String? = null,
    val error: String? = null
)

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    // 1. Declaración del Estado (Esto faltaba en tu código)
    private val _state = MutableStateFlow(UserProfileState())
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    // 2. Estado para el Logout (Necesario para tu UI)
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

            try {
                // Llamada asíncrona a Firestore
                val documentSnapshot = firestore.collection("users")
                    .document(currentUser.uid)
                    .get()
                    .await()

                if (documentSnapshot.exists()) {
                    val data = documentSnapshot.data

                    // Extracción de datos (Sin Bio)
                    val fetchedProfileUrl = data?.get("profileImageUrl") as? String
                    val fetchedCoverUrl = data?.get("coverImageUrl") as? String
                    val fetchedUsername = data?.get("username") as? String ?: "@usuario"
                    val fetchedName = data?.get("name") as? String ?: "Usuario"

                    _state.update {
                        it.copy(
                            isLoading = false,
                            profileImageUrl = fetchedProfileUrl,
                            coverImageUrl = fetchedCoverUrl,
                            username = fetchedUsername,
                            name = fetchedName
                        )
                    }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Usuario no encontrado en base de datos") }
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message ?: "Error desconocido") }
            }
        }
    }

    fun performLogout() {
        auth.signOut()
        _isLoggedOut.value = true
    }
}