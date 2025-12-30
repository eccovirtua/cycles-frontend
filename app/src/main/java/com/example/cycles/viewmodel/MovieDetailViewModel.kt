package com.example.cycles.viewmodel

import com.example.cycles.data.MovieDetailDto
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Estado de la UI
data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movie: MovieDetailDto? = null,
    val error: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val repository: RecsRepository,
    savedStateHandle: SavedStateHandle // Para recibir el ID de la navegación
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState = _uiState.asStateFlow()

    // Asumimos que la navegación pasa el argumento como "itemId"
    private val movieId: String? = savedStateHandle["itemId"]

    init {
        loadMovieDetail()
    }

    fun loadMovieDetail() {
        val id = movieId?.toIntOrNull()
        if (id == null) {
            _uiState.value = MovieDetailUiState(isLoading = false, error = "ID inválido")
            return
        }

        viewModelScope.launch {
            _uiState.value = MovieDetailUiState(isLoading = true)
            try {
                val result = repository.getMovieDetail(id)
                _uiState.value = MovieDetailUiState(isLoading = false, movie = result)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = MovieDetailUiState(isLoading = false, error = "Error al cargar: ${e.message}")
            }
        }
    }
}