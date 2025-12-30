package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.cycles.data.SearchResultItem
import com.example.cycles.utils.TmdbImageUtils
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


// Estado único para toda la pantalla
data class HomeUiState(
    val isLoading: Boolean = true,
    val topRatedList: List<SearchResultItem> = emptyList(),
    val newReleasesList: List<SearchResultItem> = emptyList(),
    val forYouList: List<SearchResultItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: RecsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {

                coroutineScope {
                    val topRatedDeferred = async { repository.getTopRated() }
                    val newReleasesDeferred = async { repository.getNewReleases() }
                    val forYouDeferred = async { repository.getForYou() }

                    // Esperamos a que lleguen las 3 (await)
                    val topRated = topRatedDeferred.await()
                    val newReleases = newReleasesDeferred.await()
                    val forYou = forYouDeferred.await()

                    // Mapeamos los datos (DTO -> UI Model)
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            topRatedList = topRated.map { it.toUiModel() },
                            newReleasesList = newReleases.map { it.toUiModel() },
                            forYouList = forYou.map { it.toUiModel() }
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Error cargando datos") }
            }
        }
    }

    // Helper para convertir DTO a SearchResultItem (reutilizamos tu clase genérica)
    private fun com.example.cycles.data.MovieSearchDto.toUiModel(): SearchResultItem {
        return SearchResultItem(
            itemId = this.id.toString(),
            title = this.title,
            subtitle = this.releaseDate?.take(4) ?: "",
            imageUrl = TmdbImageUtils.buildPosterUrl(this.posterPath),
            type = "MOVIE"
        )
    }
}

