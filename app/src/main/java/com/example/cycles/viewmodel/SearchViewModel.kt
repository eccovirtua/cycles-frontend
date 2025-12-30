package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.data.SearchResultItem
import com.example.cycles.repository.RecsRepository
import com.example.cycles.utils.TmdbImageUtils
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
    private val repository: RecsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private var searchJob: Job? = null

    fun updateQuery(newQuery: String) {
        _uiState.update { it.copy(query = newQuery, error = null) }
        searchJob?.cancel()

        if (newQuery.length >= 2) { // Bajé el límite a 2 letras para que sea más reactivo
            searchJob = viewModelScope.launch {
                delay(400) // Debounce
                performSearch()
            }
        } else {
            _uiState.update { it.copy(results = emptyList()) }
        }
    }

    fun performSearch() {
        val query = _uiState.value.query.trim()
        if (query.isEmpty()) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                // 1. LLAMADA A LA API NUEVA (Localhost -> MeiliSearch)
                val movieResults = repository.searchMovies(query)

                // 2. MAPEO: Convertir MovieSearchDto -> SearchResultItem
                // Esto adapta los datos de TMDB para que tu UI los entienda
                val mappedResults = movieResults.map { movieDto ->
                    SearchResultItem(
                        itemId = movieDto.id.toString(), // Convertimos int a string
                        title = movieDto.title,
                        subtitle = movieDto.releaseDate?.take(4) ?: "Sin año", // Solo el año
                        imageUrl = TmdbImageUtils.buildPosterUrl(movieDto.posterPath), // Construimos la URL completa
                        type = "movie" // Etiqueta para saber qué es
                    )
                }

                _uiState.update {
                    it.copy(
                        results = mappedResults,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error conectando al servidor local: ${e.message}"
                    )
                }
            }
        }
    }
}




