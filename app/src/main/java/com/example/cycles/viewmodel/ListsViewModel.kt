package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserListBasic
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListsUiState(
    val isLoading: Boolean = false,
    val lists: List<UserListBasic> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ListsViewModel @Inject constructor(
    private val repository: RecsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val myLists = repository.getMyLists()
                _uiState.update { it.copy(isLoading = false, lists = myLists) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar listas: ${e.message}") }
            }
        }
    }

    fun createList(name: String, icon: String, color: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            // Mostramos el error en el diálogo, no en la pantalla principal
            _uiState.update { it.copy(error = null) }
            try {
                repository.createList(name, icon, color)
                loadLists() // Recargamos la lista
                onComplete() // Cerramos el diálogo
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al crear: ${e.message}") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}