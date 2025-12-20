package com.example.cycles.viewmodel


import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cycles.data.UserPreferences
import com.example.cycles.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


//@HiltViewModel
//class ChooseUsernameViewModel @Inject constructor(
//    private val repo: AuthRepository,
//    savedStateHandle: SavedStateHandle,
//    private val userPreferences: UserPreferences
//): ViewModel() {
//    val name = MutableStateFlow("")
//    val isAvailable = MutableStateFlow<Boolean?>(null)
//    val isLoading = MutableStateFlow(false)
//    val error = MutableStateFlow<String?>(null)
//
//    private val token: String = savedStateHandle["token"]
//        ?: throw IllegalStateException("token missing")
//
//    fun onNameChange(v: String) {
//        name.value = v
//        error.value = null
//        isAvailable.value = null
//    }
//
//    fun checkAvailability() = viewModelScope.launch {
//        isLoading.value = true
////        val available = repo.checkUsername(name.value)
//        isLoading.value = false
//        if (available) isAvailable.value = true
//        else error.value = "Nombre de usuario no disponible"
//    }
//
//    fun saveUsername(nav: NavController) = viewModelScope.launch {
//        isLoading.value = true
//        val usernameToSave = name.value // Get current username
//        val ok = repo.updateUsername(usernameToSave, token)
//        isLoading.value = false
//        if (ok) {
//            try {
//                userPreferences.saveUsername(usernameToSave)
//            } catch (e: Exception) {
//                // Log error saving locally, but proceed with navigation
//                Log.e("ChooseUsernameVM", "Failed to save username locally: ${e.message}")
//            }
//            nav.navigate("home") { // Navigate to home after saving
//                popUpTo(nav.graph.startDestinationId) { inclusive = true } // Clear back stack
//                launchSingleTop = true
//            }
//        } else {
//            error.value = "Error guardando nombre de usuario"
//        }
//    }
//}
