package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.RecommendationItem
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FinalRecommendationsViewModel @Inject constructor(
    private val repository: RecsRepository
) : ViewModel() {

    sealed class UiState {
        object Loading : UiState()
        data class Success(val recommendations: List<RecommendationItem>) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState
    fun loadFinalRecommendations(sessionId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = repository.finalizeSession(sessionId)
                _uiState.value = UiState.Success(response.recommendations)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}
