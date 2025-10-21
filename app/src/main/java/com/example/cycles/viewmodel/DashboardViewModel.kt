package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserDashboardStats
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


// ✨ UPDATE UiState for Dashboard
data class DashboardUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val latestStats: UserDashboardStats? = null,
    val statsBefore: UserDashboardStats? = null, // Store statsBefore for animation start
    val triggerAnimation: Boolean = false // Flag to trigger animation in UI
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: RecsRepository,
    savedStateHandle: SavedStateHandle // 👈 Inject SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    // Read the navigation argument
    private val shouldAnimate: Boolean = savedStateHandle.get<Boolean>("animate") ?: false

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, triggerAnimation = false) } // Reset animation trigger
            try {
                // Fetch the absolute latest stats
                val latestStats = repository.getDashboardStats()
                var statsBeforeForAnimation: UserDashboardStats? = null

                if (shouldAnimate) {
                    // If animating, get 'statsBefore' from the cache
                    statsBeforeForAnimation = StatsCache.statsBeforeSession
                    // Clear the cache after use
                    StatsCache.statsBeforeSession = null
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        latestStats = latestStats,
                        // Use statsBefore from cache if animating, otherwise use latest (no animation)
                        statsBefore = if (shouldAnimate) statsBeforeForAnimation else latestStats,
                        triggerAnimation = shouldAnimate // Trigger animation only if flag was true
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error loading dashboard: ${e.message}") }
            }
        }
    }

    // Function to call when animation finishes (optional, to prevent re-animating on config change)
    fun animationShown() {
        _uiState.update { it.copy(triggerAnimation = false) }
    }
}
//sealed class DashboardUiState {
//    object Loading : DashboardUiState()
//    data class Success(val stats: UserDashboardStats) : DashboardUiState()
//    data class Error(val message: String) : DashboardUiState()
//}
//
//@HiltViewModel
//class DashboardViewModel @Inject constructor(
//    private val recsRepository: RecsRepository
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
//    val uiState: StateFlow<DashboardUiState> = _uiState
//
//    init {
//        fetchDashboardStats()
//    }
//
//    fun fetchDashboardStats() {
//        viewModelScope.launch {
//            _uiState.value = DashboardUiState.Loading
//            try {
//
//                val stats = recsRepository.getDashboardStats()
//                _uiState.value = DashboardUiState.Success(stats)
//            } catch (e: Exception) {
//                // Manejo de errores simple. En producción, deberías ser más específico.
//                _uiState.value = DashboardUiState.Error("Error al cargar las estadísticas: ${e.message}")
//            }
//        }
//    }
//}