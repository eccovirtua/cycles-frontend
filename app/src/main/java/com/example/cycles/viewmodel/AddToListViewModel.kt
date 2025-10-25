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

data class AddToListUiState(
    val isLoading: Boolean = false,
    val lists: List<UserListBasic> = emptyList(),
    val error: String? = null,
    val successMessage: String? = null // Para mostrar "Añadido a..."
)

@HiltViewModel
class AddToListViewModel @Inject constructor(
    private val repository: RecsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddToListUiState())
    val uiState = _uiState.asStateFlow()

    // Cargamos las listas en cuanto el VM se inicializa
    init {
        loadLists()
    }

    fun loadLists() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                val myLists = repository.getMyLists()
                _uiState.update { it.copy(isLoading = false, lists = myLists) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun addItemToList(list: UserListBasic, itemId: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, successMessage = null) }
            try {
                repository.addItemToList(list.listId, itemId)
                _uiState.update { it.copy(isLoading = false, successMessage = "Añadido a \"${list.name}\"") }
                kotlinx.coroutines.delay(1500) // Mostramos el éxtio por 1.5s
                onComplete() // Cerramos el diálogo
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}