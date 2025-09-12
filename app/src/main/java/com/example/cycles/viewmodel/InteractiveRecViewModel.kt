package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.RecommendationItem
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
    private var currentIteration: Int = 1

    sealed class UiState {
        object Loading : UiState()
        data class Seed(val seed: RecommendationItem, val iteration: Int) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    val sessionId: String?
        get() = currentSessionId

    var navigatingToFinalGrid = false

    /** Crea sesión y obtiene seed inicial */
    fun loadInitialSeed(domain: String, onSessionFinalized: (List<RecommendationItem>) -> Unit) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val sessionResponse = repo.createSession(domain)
                currentSessionId = sessionResponse.session_id
                currentDomain = domain
                currentIteration = 1

                val seed = repo.getInitialSeed(domain)
                if (seed == null) {
                    // No hay seed → finalizar inmediatamente
                    finalizeSession(onSessionFinalized)
                    return@launch
                }

                _uiState.value = UiState.Seed(seed, currentIteration)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al crear sesión")
            }
        }
    }

    /** Envía feedback y avanza iteración; finaliza sesión si ya no hay más seeds */
    fun sendFeedback(feedback: Int, onSuccess: (List<RecommendationItem>) -> Unit) {
        val sessionId = currentSessionId ?: run {
            _uiState.value = UiState.Error("Session no válida")
            return
        }
        val currentSeed = (_uiState.value as? UiState.Seed)?.seed ?: run {
            _uiState.value = UiState.Error("No hay seed activo")
            return
        }

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val newSeed = repo.sendSessionFeedback(sessionId, currentSeed.itemId, feedback)

                if (newSeed == null) {
                    // No hay siguiente seed → finalizar sesión automáticamente
                    finalizeSession(onSuccess)
                    return@launch
                }

                // Avanzamos la iteración y actualizamos estado
                currentIteration += 1
                _uiState.value = UiState.Seed(newSeed, currentIteration)

            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al enviar feedback: ${e.message}")
            }
        }
    }

    /** Finaliza sesión y devuelve lista final de recomendaciones */
    fun finalizeSession(onSuccess: (List<RecommendationItem>) -> Unit) {
        val sessionId = currentSessionId ?: return
        val domain = currentDomain ?: return

        viewModelScope.launch {
            try {
                val response = repo.finalizeSession(sessionId)
                SessionCache.saveSession(domain, sessionId)
                navigatingToFinalGrid = true
                onSuccess(response.recommendations)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al finalizar sesión: ${e.message}")
            }
        }
    }
}