package com.example.cycles.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemDetailUiState(
    val isLoading: Boolean = true,
    val itemDetails: ItemDetailResponse? = null,
    val isFavorite: Boolean = false, // Nuevo estado para favoritos
    val error: String? = null
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val repository: RecsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val itemId: String = savedStateHandle["itemId"] ?: ""
    // val itemType: ItemType = ItemType.valueOf(savedStateHandle["itemType"] ?: ItemType.BOOK.name) // Si necesitas el tipo

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (itemId.isNotBlank()) {
            loadItemDetails()
            checkFavoriteStatus() // Comprobar estado inicial de favorito
        } else {
            _uiState.update { it.copy(isLoading = false, error = "ID de item no válido") }
        }
    }

    private fun loadItemDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val details = repository.getItemDetails(itemId)
                _uiState.update { it.copy(isLoading = false, itemDetails = details) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar detalles: ${e.message}") }
            }
        }
    }

    private fun checkFavoriteStatus() {
        viewModelScope.launch {
            val isFav = repository.getFavoriteStatus(itemId)
            _uiState.update { it.copy(isFavorite = isFav) }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val isCurrentlyFavorite = currentState.isFavorite

            // Actualización optimista de la UI
            _uiState.update { it.copy(isFavorite = !isCurrentlyFavorite) }

            try {
                if (isCurrentlyFavorite) {
                    repository.removeFavorite(itemId)
                } else {
                    repository.addFavorite(itemId)
                }
                // Opcional: Volver a verificar el estado por si acaso
                // checkFavoriteStatus()
            } catch (e: Exception) {
                // Revertir en caso de error
                _uiState.update {
                    it.copy(
                        isFavorite = isCurrentlyFavorite, // Revertir estado
                        error = "Error al ${if (isCurrentlyFavorite) "quitar" else "añadir"} favorito: ${e.message}"
                    )
                }
            }
        }
    }
}