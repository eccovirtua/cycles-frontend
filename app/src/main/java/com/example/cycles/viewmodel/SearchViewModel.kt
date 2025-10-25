package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.data.SearchResultItem
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class SearchUiState(
    val query: String = "",
    val results: List<SearchResultItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,

    // Para la pantalla de detalle
    val currentItemDetail: ItemDetailResponse? = null,
    val isDetailLoading: Boolean = false,
    val detailError: String? = null
)


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: RecsRepository // Inyectar el repositorio
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private var searchJob: Job? = null // Para debouncing

    fun updateQuery(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, error = null) }
        // Cancelar búsqueda anterior si el usuario sigue escribiendo
        searchJob?.cancel()
        if (newQuery.length >= 3) {
            // Iniciar búsqueda con debounce (ej: 500ms)
            searchJob = viewModelScope.launch {
                delay(500)
                performSearch()
            }
        } else {
            // Limpiar resultados si la query es corta
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    /**
     * Llama a la API real para buscar ítems.
     */
    fun performSearch() {
        val query = _uiState.value.query.trim()
        if (query.length < 3) return // Ya validado, pero doble check

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val response = repository.searchItems(query)
                val uniqueResults = response.results.distinctBy { it.itemId }
                _uiState.update {
                    it.copy(
                        results = uniqueResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error en la búsqueda: ${e.message}"
                    )
                }
            }
        }
    }
}
