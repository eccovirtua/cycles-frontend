package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoadingUsage: Boolean = false,
    val remainingSessions: Int? = null, // null hasta que se cargue
    val error: String? = null
)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecsRepository
    ): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    // Estado expuesto al Composable (solo lectura)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()

    // Propiedad calculada para la UI
    val isLimitReached: StateFlow<Boolean> = uiState
        .map { it.remainingSessions == 0 } // Es true si remaining es 0
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    init {
        loadUsageStatus() // Cargar al iniciar
    }
    fun loadUsageStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingUsage = true, error = null) }
            try {
                val usage = repository.getUserUsage()
                _uiState.update { it.copy(isLoadingUsage = false, remainingSessions = usage.remainingToday) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingUsage = false, error = "Error al cargar límite: ${e.message}") }
                // Considera poner remainingSessions a 0 si falla la carga para bloquear por defecto
                // _uiState.update { it.copy(isLoadingUsage = false, error = "...", remainingSessions = 0) }
            }
        }
    }
}


