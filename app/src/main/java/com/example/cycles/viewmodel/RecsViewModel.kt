package com.example.cycles.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.RecommendResponse
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecsViewModel @Inject constructor(
    private val repo: RecsRepository
) : ViewModel() {

    private val _recs = MutableStateFlow<RecommendResponse?>(null)
    val recs: StateFlow<RecommendResponse?> = _recs

    fun loadRecs(itemId: Int, topN: Int = 5) {
        viewModelScope.launch {
            try {
                _recs.value = repo.fetchRecs(itemId, topN)
            } catch (e: Exception) {
                // manejar error
                _recs.value = null
            }
        }
    }
}