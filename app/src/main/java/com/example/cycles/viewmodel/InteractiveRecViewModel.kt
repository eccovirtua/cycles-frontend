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
    private var currentItemId: String? = null



    sealed class UiState {
        object Loading           : UiState()
        data class Success(val seed: RecommendationItem) : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
    val uiState: StateFlow<UiState> = _uiState

    /** Carga el ítem semilla inicial para este usuario y dominio */
    fun loadInitialSeed(domain: String) {
        val current = (_uiState.value as? UiState.Success)?.seed
        if (current != null) return

        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val seed = repo.getInitialSeed(domain) //llama  get /seed/domain
                currentItemId = seed.itemId
                _uiState.value = UiState.Success(seed)
                currentDomain = domain
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar semilla")
            }
        }
    }

    /** Envía feedback y actualiza el ítem semilla con la respuesta del backend */
    fun sendFeedback(feedback: Int) {
        val domain = currentDomain ?: return
        val current = (_uiState.value as? UiState.Success)?.seed ?: return

        viewModelScope.launch {

            _uiState.value = UiState.Loading
            try {
                val newSeed = repo.sendFeedback(
                    domain,
                    current.itemId,
                    feedback
                )
                currentItemId = newSeed.itemId
                _uiState.value = UiState.Success(newSeed)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al procesar feedback")
            }
        }
    }


    fun resetRecommendations() { //para reinicar las recomendaciones
        val domain = currentDomain ?: return
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val seed = repo.reset(domain)
                currentItemId = seed.itemId
                _uiState.value = UiState.Success(seed)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al reiniciar")
            }
        }
    }
}