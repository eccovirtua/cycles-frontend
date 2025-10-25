package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ItemDetailUiState(
    val currentItemDetail: ItemDetailResponse? = null,
    val isDetailLoading: Boolean = false,
    val detailError: String? = null
)

@HiltViewModel
class ItemDetailViewModel @Inject constructor(
    private val recsRepository: RecsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItemDetailUiState())
    val uiState: StateFlow<ItemDetailUiState> = _uiState.asStateFlow()

    fun loadItemDetails(itemId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetailLoading = true, detailError = null) }
            try {
                // Asumiendo que el repositorio tiene un método para obtener detalles.
                // Si el método tiene otro nombre, necesitaré ajustarlo.
                val result = recsRepository.getItemDetails(itemId)
                _uiState.update {
                    it.copy(
                        isDetailLoading = false,
                        currentItemDetail = result
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDetailLoading = false,
                        detailError = e.message ?: "An unknown error occurred"
                    )
                }
            }
        }
    }

    fun clearItemDetails() {
        _uiState.update { it.copy(currentItemDetail = null, detailError = null) }
    }
}
