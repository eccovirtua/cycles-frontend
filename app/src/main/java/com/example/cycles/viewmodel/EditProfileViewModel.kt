package com.example.cycles.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserUpdateRequest
import com.example.cycles.repository.UserRepository
import com.example.cycles.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditProfileState(
    val isLoading: Boolean = false,
    val country: String = "",
    val isAgeVisible: Boolean = true,
    val currentProfileUrl: String? = null, // URL remota actual
    val currentCoverUrl: String? = null,   // URL remota actual
    val newProfileUri: Uri? = null,        // URI local si seleccionó nueva foto
    val newCoverUri: Uri? = null,          // URI local si seleccionó nueva portada
    val error: String? = null,
    val isSavedSuccess: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileState())
    val state = _state.asStateFlow()

    init {
        loadCurrentUserData()
    }

    private fun loadCurrentUserData() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = userRepository.getUserProfile(uid)
            result.onSuccess { user ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        country = user.country ?: "",
                        // Asumimos que user.show_age viene del backend, si no, por defecto true
                        // isAgeVisible = user.show_age ?: true,
                        currentProfileUrl = user.profilePictureUrl,
                        currentCoverUrl = user.coverImageUrl
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false, error = "Error al cargar datos") }
            }
        }
    }

    // --- Eventos de UI ---
    fun onCountryChange(newValue: String) { _state.update { it.copy(country = newValue) } }
    fun onAgeVisibilityChange(newValue: Boolean) { _state.update { it.copy(isAgeVisible = newValue) } }

    fun onNewProfileImageSelected(uri: Uri?) { _state.update { it.copy(newProfileUri = uri) } }
    fun onNewCoverImageSelected(uri: Uri?) { _state.update { it.copy(newCoverUri = uri) } }

    fun saveChanges() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                // 1. Subir Foto Perfil (Solo si cambió)
                var finalProfileUrl = _state.value.currentProfileUrl
                if (_state.value.newProfileUri != null) {
                    val uploadedUrl = authRepository.uploadProfilePicture(uid, _state.value.newProfileUri!!)
                    if (uploadedUrl != null) finalProfileUrl = uploadedUrl
                }

                // 2. Subir Foto Portada (Solo si cambió)
                var finalCoverUrl = _state.value.currentCoverUrl
                if (_state.value.newCoverUri != null) {
                    val uploadedUrl = authRepository.uploadCoverPicture(uid, _state.value.newCoverUri!!)
                    if (uploadedUrl != null) finalCoverUrl = uploadedUrl
                }

                // 3. Crear Request para Backend
                val request = UserUpdateRequest(
                    country = _state.value.country,
                    show_age = _state.value.isAgeVisible,
                    profile_picture = finalProfileUrl,
                    cover_image = finalCoverUrl
                )

                // 4. Enviar a Mongo
                val success = authRepository.updateUserBackend(uid, request)

                if (success) {
                    _state.update { it.copy(isLoading = false, isSavedSuccess = true) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Error al guardar en servidor") }
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}