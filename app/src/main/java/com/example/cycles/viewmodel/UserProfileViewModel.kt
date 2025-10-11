package com.example.cycles.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.cycles.data.SessionCacheContract

// Definición de eventos de única vez (Side Effects)
sealed class UserProfileEvent {
    object NavigateBack : UserProfileEvent()
}

// 🎯 Modelo de Datos para la UI (UserProfileState)
data class UserProfileState(
    val isLoading: Boolean = false,
    val username: String = "",
    val name: String = "",
    val bio: String = "",
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val profileImageUrl: String = "",
    val coverImageUrl: String = "",
    val sectionIndex: Int = 0,

    val error: String? = null,

    // Campos para la edición y previsualización
    val newName: String = "",
    val newBio: String = "",
    val newProfileUri: Uri? = null // URI local de la imagen seleccionada
)


@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepositoryContract,
    // CLAVE: Inyección de SessionCacheContract
    private val sessionCache: SessionCacheContract
) : ViewModel() {

    // Se inicializa el StateFlow ANTES del bloque init
    private val _state = MutableStateFlow(UserProfileState(isLoading = true))
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<UserProfileEvent>()
    val events: SharedFlow<UserProfileEvent> = _events.asSharedFlow()

    private val currentUserId: String = "current_user_id"

    init {
        loadUserProfile()
    }


    // 🛑 CORRECCIÓN: Se añade el parámetro shouldNavigateBack a la firma de la función.
    fun loadUserProfile(shouldNavigateBack: Boolean = false) {
        // La recarga debe ejecutarse en el hilo principal del ViewModel, pero las llamadas al repositorio no.
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            var remotePhotoUrl = "https://picsum.photos/200/200?random=1"
            var loadError: String? = null

            try {
                // 🛑 Se mueve la llamada de I/O al Dispatchers.IO
                val photoData = withContext(Dispatchers.IO) {
                    profileRepository.fetchProfilePhoto(currentUserId)
                }
                remotePhotoUrl = photoData.profileImageUrl
            } catch (e: Exception) {
                // Manejo de error (por ejemplo, el 403)
                loadError = "Error al cargar foto: ${e.message}"
            }

            // Usamos la instancia inyectada para el caché
            // sessionCache (DataStore) es suspend y ya usa Dispatchers.IO internamente
            val localName = sessionCache.getLocalName() ?: "Nombre de Usuario"
            val localBio = sessionCache.getLocalBio() ?: "Biografía local"

            // Actualización del estado (que dispara la actualización de la UI)
            _state.update {
                it.copy(
                    isLoading = false,
                    profileImageUrl = remotePhotoUrl,
                    name = localName, // Nuevo nombre actualizado
                    bio = localBio, // Nueva bio actualizada
                    newName = localName,
                    newBio = localBio,
                    username = "@CyclesCEO",
                    coverImageUrl = "https://picsum.photos/800/200?random=2",
                    error = loadError
                )
            }

            // 🛑 Se dispara la navegación SOLO después de que el estado se actualizó.
            if (shouldNavigateBack) {
                _events.emit(UserProfileEvent.NavigateBack)
            }
        }
    }


    fun onNameChange(name: String) {
        _state.update { it.copy(newName = name) }
    }

    fun onBioChange(bio: String) {
        _state.update { it.copy(newBio = bio) }
    }

    fun onProfilePhotoSelected(uri: Uri?) {
        _state.update { it.copy(newProfileUri = uri) }
    }

    /**
     * Guarda los cambios del perfil.
     */
    fun saveProfileChanges() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val currentState = _state.value
            val photoUri = currentState.newProfileUri

            try {
                // 🛑 Se mueve la llamada de I/O al Dispatchers.IO
                if (photoUri != null) {
                    withContext(Dispatchers.IO) {
                        profileRepository.uploadImageAndGetUrl(currentUserId, photoUri)
                    }
                }

                // sessionCache.saveProfileMetadata es suspend y lo hace en segundo plano
                sessionCache.saveProfileMetadata(
                    name = currentState.newName,
                    bio = currentState.newBio
                )

                // Después de guardar, recargamos (lo que actualizará la UI) y navegamos.
                loadUserProfile(shouldNavigateBack = true)

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al guardar el perfil: ${e.message}"
                    )
                }
            }
        }
    }


    fun onSectionSelected(index: Int) {
        _state.update { it.copy(sectionIndex = index) }
    }
}
