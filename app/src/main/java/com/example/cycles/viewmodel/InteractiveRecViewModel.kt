package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.RecommendationItem
import com.example.cycles.data.FinalListResponse
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
        data class Success(val seed: RecommendationItem, val iteration: Int) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    /** Crea la sesión y obtiene el primer seed */
    fun loadInitialSeed(domain: String) {
        if (currentSessionId != null) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // crear sesión (necesitamos el sessionId)
                val sessionResponse: SessionCreateResponse = repo.createSession(domain)
                currentSessionId = sessionResponse.session_id
                currentDomain = domain
                currentIteration = 0

                // obtener seed inicial (usa el endpoint /seed/{domain} que ya tienes)
                val seed: RecommendationItem = repo.getInitialSeed(domain)
                _uiState.value = UiState.Success(seed, currentIteration)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al crear sesión")
            }
        }
    }

    /** Envía feedback (usa la sesión) y actualiza el seed siguiente */
    fun sendFeedback(feedback: Int) {
        val sessionId = currentSessionId ?: return
        val current = (_uiState.value as? UiState.Success)?.seed ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                // retorna el nuevo seed (RecommendationItem)
                val newSeed: RecommendationItem = repo.sendSessionFeedback(sessionId, current.itemId, feedback)
                // incrementamos contador local de iteraciones
                currentIteration += 1
                _uiState.value = UiState.Success(newSeed, currentIteration)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al procesar feedback")
            }
        }
    }

    /** Reinicia la sesión y obtiene un seed limpio */
    fun resetRecommendations() {
        val sessionId = currentSessionId ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val seed: RecommendationItem = repo.resetSession(sessionId)
                currentIteration = 0
                _uiState.value = UiState.Success(seed, currentIteration)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al reiniciar sesión")
            }
        }
    }

    /** Finaliza la sesión y devuelve la lista final (20 items). onSuccess recibe la lista */
    fun finalizeSession(onSuccess: (List<RecommendationItem>) -> Unit) {
        val sessionId = currentSessionId ?: return

        viewModelScope.launch {
            try {
                val response: FinalListResponse = repo.finalizeSession(sessionId)
                onSuccess(response.recommendations)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al finalizar sesión: ${e.message}")
            }
        }
    }
}