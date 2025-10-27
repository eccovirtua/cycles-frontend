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
//        if (sessionId == currentSessionId) return
//        currentSessionId = sessionId
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            try {
//                val statsBefore = StatsCache.statsBeforeSession
//
//                val response = repository.finalizeSession(sessionId)
//                sessionQualityScore = response.sessionAvgQuality
//
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        recommendations = response.recommendations,
//                        statsBeforeSession = statsBefore
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.update{ it.copy( isLoading = false, error = e.message ?: "Error desconocido")}
//            }
//        }
//    }
fun loadFinalRecommendations(sessionId: String) {
    // Evita recargar si ya tenemos datos para esta sesión Y stats 'before' está presente
    if (sessionId == currentSessionId && _uiState.value.statsBeforeSession != null) {
        _uiState.update { it.copy(isLoading = false) } // Asegura no mostrar 'loading'
        return
    }
    currentSessionId = sessionId

    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }
        try {
            // ✅ PASO 1: Obtener las stats "antes" directamente AHORA, ANTES de finalizar.
            val statsBefore = StatsCache.statsBeforeSession

            // PASO 2: Finalizar la sesión (esto actualiza las stats en el backend).
            val response = repository.finalizeSession(sessionId)
            sessionQualityScore = response.sessionAvgQuality // Guardamos la calidad específica

            // PASO 3: Actualizar el estado con las stats "antes" frescas y las recomendaciones.
            _uiState.update {
                it.copy(
                    isLoading = false,
                    recommendations = response.recommendations,
                    statsBeforeSession = statsBefore // Guardamos las stats 'antes' correctas.
                )
            }
        } catch (e: Exception) {
            _uiState.update{ it.copy( isLoading = false, error = e.message ?: "Error desconocido")}
        }
    }
}

//    fun onRestartClicked() {
//        viewModelScope.launch {
//            try {
//                val statsAfter = repository.getDashboardStats()
//                val modifiedStatsAfter = statsAfter.copy(
//                    totalAvgQualityScore = sessionQualityScore
//                )
//                _uiState.update {
//                    it.copy(
//                        statsAfterSession = modifiedStatsAfter,
//                        shouldShowStatsPopup = true // Esto activará el pop-up en la UI
//                    )
//                }
//            } catch (e: Exception) {
//                _uiState.update { it.copy(error = "No se pudieron cargar las estadísticas finales: ${e.message}") }
//            }
//        }
//    }
fun onRestartClicked() {
    // 🎯 PASO 1: Resetear explícitamente a false y limpiar errores previos
    _uiState.update { it.copy(shouldShowStatsPopup = false, error = null) }

    // PASO 2: Lanzar la corutina para obtener datos y luego poner a true
    viewModelScope.launch {
        try {
            val statsAfter = repository.getDashboardStats()
            val modifiedStatsAfter = statsAfter.copy(
                totalAvgQualityScore = sessionQualityScore
            )

            // PASO 3: Actualizar estado con datos y poner shouldShowStatsPopup a true
            _uiState.update {
                it.copy(
                    statsAfterSession = modifiedStatsAfter,
                    shouldShowStatsPopup = true // Ahora sí activamos el pop-up
                )
            }

        } catch (e: Exception) {
            // Asegurarse de que quede en false si hay error
            _uiState.update { it.copy(error = "No se pudieron cargar las estadísticas finales: ${e.message}", shouldShowStatsPopup = false) }
        }
    }
}



    fun dismissStatsPopup() {
        _uiState.update { it.copy(shouldShowStatsPopup = false) }
    }

}