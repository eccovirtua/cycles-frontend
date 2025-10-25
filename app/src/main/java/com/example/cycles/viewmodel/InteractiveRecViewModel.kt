package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.RecommendationItem
import com.example.cycles.data.SessionStateResponse
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.HttpException

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
        data class Final(val recommendations: List<RecommendationItem>) : UiState()
        data class Error(val message: String) : UiState()
        data class ErrorLimitReached(val message: String) : UiState()

    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    val sessionId: String?
        get() = currentSessionId


    fun createSession(domain: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val statsBefore = repo.getDashboardStats()
                StatsCache.statsBeforeSession = statsBefore


                val resp = repo.createSession(domain)
                currentDomain = domain
                currentSessionId = resp.session_id
                currentIteration = 1
                SessionCache.saveSession(domain, resp.session_id)
                SessionCache.saveLastDomain(domain)
                _uiState.value = UiState.Seed(resp.seed, currentIteration)

            } catch (e: Exception) {
                if (e is HttpException && e.code() == 429) {

                    _uiState.value = UiState.ErrorLimitReached(e.message ?: "Límite diario de sesiones alcanzado.")
                } else {
                    _uiState.value = UiState.Error("Error creando sesión: ${e.message}")
                }
            }
        }
    }

    fun sendFeedback(feedback: Int) {
        val sid = currentSessionId ?: return
        val currentSeed = (_uiState.value as? UiState.Seed)?.seed ?: return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val nextSeed: RecommendationItem? =
                    repo.sendFeedback(sid, currentSeed.itemId, feedback)

                if (nextSeed == null) {
                    finalizeSession()
                } else {
                    currentIteration++
                    _uiState.value = UiState.Seed(nextSeed, currentIteration)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al enviar feedback: ${e.message}")
            }
        }
    }

    private fun finalizeSession() {
        val sid = currentSessionId ?: return

        viewModelScope.launch {
            try {
                val resp = repo.finalizeSession(sid)
                _uiState.value = UiState.Final(resp.recommendations)
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error al finalizar sesión: ${e.message}")
            }
        }
    }

    suspend fun getSessionState(sessionId: String): SessionStateResponse {
        return repo.getSessionState(sessionId)
    }

    fun resumeSession(sessionId: String) {
        currentSessionId = sessionId
    }

    fun loadExistingSeed(seed: RecommendationItem, iteration: Int) {
        currentIteration = iteration
        _uiState.value = UiState.Seed(seed, iteration)
    }
}