package com.example.cycles.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.SearchResultItem
import dagger.hilt.android.lifecycle.HiltViewModel
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
import com.example.cycles.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext

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
    private val userPreferences: UserPreferences,
    private val repository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(UserProfileState(isLoading = true))
    val state: StateFlow<UserProfileState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<UserProfileEvent>()
    val events: SharedFlow<UserProfileEvent> = _events.asSharedFlow()

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()


    init {
        // Observe username changes continuously
        observeUsername() // Call the new function

        loadUserProfileData() // Load photo, name, bio
    }

    fun performLogout() {
        viewModelScope.launch {
            // Llamamos al repo pasando el contexto inyectado
            repository.logout(context)

            // Avisamos a la UI
            _isLoggedOut.value = true
        }
    }

    private fun observeUsername() {
        viewModelScope.launch {
            userPreferences.username
                .collect { fetchedUsername ->
                    Log.d(
                        "UserProfileVM",
                        "Collected username from Prefs: $fetchedUsername"
                    ) // <-- ADD LOG
                    _state.update {
                        it.copy(username = fetchedUsername?.let { "@$it" } ?: "@usuario")
                    }
                }
        }
    }

    fun loadUserProfileData(shouldNavigateBack: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            var remotePhotoUrl = "..."
            val loadError: String? = null

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
}