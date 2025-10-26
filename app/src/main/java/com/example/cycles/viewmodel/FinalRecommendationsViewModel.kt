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
import com.example.cycles.data.UserDashboardStats
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class FinalRecommendationsViewModel @Inject constructor(
    private val repository: RecsRepository
) : ViewModel() {

    data class UiState(
        val isLoading: Boolean = true,
        val recommendations: List<RecommendationItem> = emptyList(),
        val error: String? = null,
        val shouldShowStatsPopup: Boolean = false,
        val statsBeforeSession: UserDashboardStats? = null,
        val statsAfterSession: UserDashboardStats? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var currentSessionId: String = ""
    private var sessionQualityScore: Float = 0.0f


//    fun loadFinalRecommendations(sessionId: String) {
//        // Avoid reloading if we already have data and statsBefore for this specific session ID
//        if (sessionId == currentSessionId && _uiState.value.statsBeforeSession != null) {
//            _uiState.update { it.copy(isLoading = false) } // Ensure loading indicator is off
//            return
//        }
//        currentSessionId = sessionId
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            try {
//                val statsBefore = repository.getDashboardStats()
//
//                val response = repository.finalizeSession(sessionId)
//                sessionQualityScore = response.sessionAvgQuality // Store session-specific quality
//
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        recommendations = response.recommendations,
//                        statsBeforeSession = statsBefore // Store the correct 'before' stats.
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.update{ it.copy( isLoading = false, error = e.message ?: "Error desconocido")}
//            }
//        }
//    }

    fun loadFinalRecommendations(sessionId: String) {
        if (sessionId == currentSessionId) return
        currentSessionId = sessionId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val statsBefore = StatsCache.statsBeforeSession

                val response = repository.finalizeSession(sessionId)
                sessionQualityScore = response.sessionAvgQuality

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recommendations = response.recommendations,
                        statsBeforeSession = statsBefore
                    )
                }
            } catch (e: Exception) {
                _uiState.update{ it.copy( isLoading = false, error = e.message ?: "Error desconocido")}
            }
        }
    }

    fun onRestartClicked() {
        viewModelScope.launch {
            try {
                val statsAfter = repository.getDashboardStats()
                val modifiedStatsAfter = statsAfter.copy(
                    totalAvgQualityScore = sessionQualityScore
                )
                _uiState.update {
                    it.copy(
                        statsAfterSession = modifiedStatsAfter,
                        shouldShowStatsPopup = true // Esto activará el pop-up en la UI
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "No se pudieron cargar las estadísticas finales: ${e.message}") }
            }
        }
    }



    fun dismissStatsPopup() {
        _uiState.update { it.copy(shouldShowStatsPopup = false) }
    }

}