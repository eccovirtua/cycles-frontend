package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.RecommendationItem
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InteractiveRecViewModel @Inject constructor(
    private val repo: RecsRepository
) : ViewModel() {

    private var currentDomain: String? = null
    private var currentSessionId: String? = null
    private var currentIteration: Int = 0

    sealed class UiState {
        object Loading : UiState()
        data class Seed(val seed: RecommendationItem, val iteration: Int) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState
    val sessionId: String?
        get() = currentSessionId

    /** Crea sesión y obtiene seed inicial */
    fun loadInitialSeed(domain: String) {
        if (currentSessionId != null) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val sessionResponse: SessionCreateResponse = repo.createSession(domain)
                currentSessionId = sessionResponse.session_id
                currentDomain = domain
                currentIteration = 0

                val seed = repo.getInitialSeed(domain)
                _uiState.value = UiState.Seed(seed, currentIteration)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al crear sesión")
            }
        }
    }

    /** Envía feedback y avanza iteración; llama callback si se finaliza la sesión */
    fun sendFeedback(feedback: Int, onSessionFinalized: (List<RecommendationItem>) -> Unit) {
        val sessionId = currentSessionId ?: return
        val currentSeed = (_uiState.value as? UiState.Seed)?.seed ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newSeed = repo.sendSessionFeedback(sessionId, currentSeed.itemId, feedback)

                if (newSeed != null) {
                    currentIteration += 1
                    _uiState.value = UiState.Seed(newSeed, currentIteration)
                } else {
                    // ya no hay más seeds → finalizar sesión automáticamente
                    finalizeSession(onSessionFinalized)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al enviar feedback: ${e.message}")
            }
        }
    }

    /** Finaliza sesión y devuelve lista final de recomendaciones */
    fun finalizeSession(onSuccess: (List<RecommendationItem>) -> Unit) {
        val sessionId = currentSessionId ?: return

        viewModelScope.launch {
            try {
                val response = repo.finalizeSession(sessionId)
                onSuccess(response.recommendations)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al finalizar sesión: ${e.message}")
            }
        }
    }
}