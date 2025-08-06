//package com.example.cycles.viewmodel
//
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.cycles.data.RecommendationItem
//import com.example.cycles.repository.RecsRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//
//@HiltViewModel
//class MovieRecViewModel @Inject constructor(
//    private val repo: RecsRepository
//) : ViewModel() {
//
//    sealed class UiState {
//        object Idle : UiState()
//        object Loading : UiState()
//        data class Success(val list: List<RecommendationItem>) : UiState()
//        data class Error(val message: String) : UiState()
//    }
//
//    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
//    val uiState: StateFlow<UiState> = _uiState
//
//    fun loadRecommendations(itemId: String, topN: Int = 5) {
//        viewModelScope.launch {
//            _uiState.value = UiState.Loading
//            try {
//                val response = repo.fetchRecs(itemId, topN)
//                _uiState.value = UiState.Success(response.recommendations)
//            } catch (e: Exception) {
//                _uiState.value = UiState.Error(e.message ?: "Error desconocido")
//            }
//        }
//    }
//}