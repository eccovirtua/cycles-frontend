package com.example.cycles.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.SearchResultItem
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
import javax.inject.Inject
import com.example.cycles.data.UserListBasic
import com.example.cycles.data.UserPreferences
import com.example.cycles.repository.RecsRepository

// Definición de eventos de única vez (Side Effects)
sealed class UserProfileEvent {
    object NavigateBack : UserProfileEvent()
}


data class UserProfileState(
    val isLoading: Boolean = false,
    val username: String = "", // Ahora viene de UserPreferences
    val name: String = "", // Nombre (de SessionCache)
    val bio: String = "", // Bio (de SessionCache)
    val followersCount: Int = 0, // Placeholder
    val followingCount: Int = 0, // Placeholder
    val profileImageUrl: String = "https://picsum.photos/800/200?random=2", // De ProfileRepository
    val coverImageUrl: String = "https://picsum.photos/800/200?random=2", // Placeholder

    // Pestañas
    val sectionIndex: Int = 0, // 0: Listas, 1: Archivadas, 2: Favoritos
    val activeLists: List<UserListBasic> = emptyList(),
    val archivedLists: List<UserListBasic> = emptyList(),
    val favoriteItems: List<SearchResultItem> = emptyList(),
    val isLoadingSection: Boolean = false, // Para carga específica de pestañas

    val error: String? = null,

    // Campos para la edición (sin cambios)
    val newName: String = "",
    val newBio: String = "",
    val newProfileUri: Uri? = null
)


@HiltViewModel
class UserProfileViewModel @Inject constructor(
//    private val sessionCache: SessionCacheContract,
    private val userPreferences: UserPreferences, // <-- Inyectar UserPreferences
    private val recsRepository: RecsRepository   // <-- Inyectar RecsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState(isLoading = true))
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<UserProfileEvent>()
    val events: SharedFlow<UserProfileEvent> = _events.asSharedFlow()

    private val currentUserId: String = "current_user_id" // Placeholder

    init {
        // Observe username changes continuously
        observeUsername() // Call the new function

        loadUserProfileData() // Load photo, name, bio
        loadSectionData(0)
    }

    private fun observeUsername() {
        viewModelScope.launch {
            userPreferences.username
                .collect { fetchedUsername ->
                    Log.d("UserProfileVM", "Collected username from Prefs: $fetchedUsername") // <-- ADD LOG
                    _state.update {
                        it.copy(username = fetchedUsername?.let { "@$it" } ?: "@usuario")
                    }
                }
        }
    }

    // Renamed from loadUserProfile to avoid confusion
    fun loadUserProfileData(shouldNavigateBack: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            var remotePhotoUrl = "..."
            var loadError: String? = null

//            try {
//                val photoData = withContext(Dispatchers.IO) {
//                    profileRepository.fetchProfilePhoto(currentUserId)
//                }
//                remotePhotoUrl = photoData.profileImageUrl
//
//            } catch (e: Exception) {
//                loadError = "Error al cargar foto: ${e.message}" // Error only for photo now
//            }
//
//            val localName = sessionCache.getLocalName() ?: "Nombre"
//            val localBio = sessionCache.getLocalBio() ?: "Biografía"

//            _state.update {
//                it.copy(
//                    isLoading = false, // Set loading false here
//                    profileImageUrl = remotePhotoUrl,
//                    name = localName,
//                    bio = localBio,
//                    // username is now handled by observeUsername() // <-- REMOVE USERNAME UPDATE
//                    newName = localName,
//                    newBio = localBio,
//                    error = loadError // Error might still occur for photo
//                )
//            }

            if (shouldNavigateBack) {
                _events.emit(UserProfileEvent.NavigateBack)
            }
        }
    }

    // --- Lógica de Pestañas ---

    fun onSectionSelected(index: Int) {
        if (_state.value.sectionIndex == index) return // No recargar si ya está seleccionada
        _state.update { it.copy(sectionIndex = index) }
        loadSectionData(index) // Cargar datos para la nueva pestaña
    }

    private fun loadSectionData(index: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingSection = true, error = null) } // Indicador de carga para la sección
            try {
                when (index) {
                    0 -> { // Listas Activas
                        val lists = recsRepository.getMyLists(archived = false)
                        _state.update { it.copy(isLoadingSection = false, activeLists = lists) }
                    }
                    1 -> { // Listas Archivadas
                        val lists = recsRepository.getMyLists(archived = true)
                        _state.update { it.copy(isLoadingSection = false, archivedLists = lists) }
                    }
                    2 -> { // Favoritos
                        val favs = recsRepository.getFavorites()
                        _state.update { it.copy(isLoadingSection = false, favoriteItems = favs) }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingSection = false, error = "Error al cargar sección: ${e.message}") }
            }
        }
    }


    // --- Funciones de Edición (sin cambios) ---
    fun onNameChange(name: String) {
        _state.update { it.copy(newName = name) }
    }

    fun onBioChange(bio: String) {
        _state.update { it.copy(newBio = bio) }
    }

    fun onProfilePhotoSelected(uri: Uri?) {
        _state.update { it.copy(newProfileUri = uri) }
    }

//    fun saveProfileChanges() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true, error = null) }
//            val currentState = _state.value
//            val photoUri = currentState.newProfileUri
//            try {
//                if (photoUri != null) {
//                    withContext(Dispatchers.IO) {
//                        profileRepository.uploadImageAndGetUrl(currentUserId, photoUri)
//                    }
//                }
//                // Guardar Nombre y Bio
//                sessionCache.saveProfileMetadata(
//                    name = currentState.newName,
//                    bio = currentState.newBio
//                )
//                // NO guardamos username aquí, se guarda en su propio flujo
//
//                // Recargar perfil (sin navegación) y luego navegar
//                loadUserProfileData(shouldNavigateBack = true)
//
//            } catch (e: Exception) {
//                _state.update {
//                    it.copy(isLoading = false, error = "Error al guardar: ${e.message}")
//                }
//            }
//        }
//    }


    fun archiveList(listId: String) {
        viewModelScope.launch {
            val currentLists = _state.value.activeLists
            
            _state.update {
                it.copy(
                    activeLists = currentLists.filterNot { l -> l.listId == listId },
                    
                )
            }
            try {
                recsRepository.archiveList(listId)
               
                loadSectionData(0)
            } catch (e: Exception) {
                // Revertir si hay error
                _state.update {
                    it.copy(
                        activeLists = currentLists, // Restaurar
                        error = "Error al archivar: ${e.message}"
                    )
                }
            }
        }
    }

    fun unarchiveList(listId: String) {
        viewModelScope.launch {
            val currentArchived = _state.value.archivedLists
            // Actualización optimista
            _state.update {
                it.copy(
                    archivedLists = currentArchived.filterNot { l -> l.listId == listId }
                )
            }
            try {
                recsRepository.unarchiveList(listId)
                // Recargar la sección actual (que debería ser 'Archivadas')
                loadSectionData(1) // O _state.value.sectionIndex
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        archivedLists = currentArchived, // Restaurar
                        error = "Error al desarchivar: ${e.message}"
                    )
                }
            }
        }
    }
}
