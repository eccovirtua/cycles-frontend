package com.example.cycles.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserListDetail
import com.example.cycles.repository.RecsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListDetailUiState(
    val isLoading: Boolean = true,
    val listDetails: UserListDetail? = null,
    val error: String? = null
)

// Eventos para acciones de una sola vez (como navegar)
sealed class ListDetailEvent {
    object ListDeleted : ListDetailEvent()
}

@HiltViewModel
class ListDetailViewModel @Inject constructor(
    private val repository: RecsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val listId: String = savedStateHandle["listId"] ?: ""

    private val _uiState = MutableStateFlow(ListDetailUiState())
    val uiState = _uiState.asStateFlow()

    // SharedFlow para eventos de navegación
    private val _eventFlow = MutableSharedFlow<ListDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        if (listId.isNotBlank()) {
            loadListDetails()
        } else {
            _uiState.update { it.copy(isLoading = false, error = "ID de lista no válido") }
        }
    }

    fun loadListDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val details = repository.getListDetails(listId)
                _uiState.update { it.copy(isLoading = false, listDetails = details) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar detalles: ${e.message}") }
            }
        }
    }

    fun removeItem(itemId: String) {
        viewModelScope.launch {
            val originalDetails = _uiState.value.listDetails ?: return@launch

            // 1. Actualización optimista de la UI (removemos el item al instante)
            _uiState.update {
                it.copy(
                    listDetails = originalDetails.copy(
                        items = originalDetails.items.filterNot { item -> item.itemId == itemId },
                        itemCount = originalDetails.itemCount - 1
                    )
                )
            }

            // 2. Llamada a la API
            try {
                // La API nos devuelve el estado actualizado, pero ya lo tenemos
                repository.removeItemFromList(listId, itemId)
                // Opcional: recargar `loadListDetails()` si queremos estar 100% seguros
            } catch (e: Exception) {
                // 3. Revertir si hay un error
                _uiState.update {
                    it.copy(
                        error = "Error al eliminar: ${e.message}",
                        listDetails = originalDetails // Revertimos al estado original
                    )
                }
            }
        }
    }

    fun updateList(name: String, icon: String, color: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(error = null) }
            try {
                val updatedList = repository.updateList(listId, name, icon, color)
                // Actualizamos la UI con los nuevos datos
                _uiState.update {
                    it.copy(
                        listDetails = it.listDetails?.copy(
                            name = updatedList.name,
                            iconName = updatedList.iconName ?: "default", // Proporciona un valor por defecto si es null
                            colorHex = updatedList.colorHex ?: "#FFFFFF"
                        )
                    )
                }
                onComplete() // Cerramos el diálogo de edición
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al actualizar: ${e.message}") }
            }
        }
    }

    fun deleteList() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteList(listId)
                // Emitimos el evento para que la UI navegue hacia atrás
                _eventFlow.emit(ListDetailEvent.ListDeleted)
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Error al borrar: ${e.message}") }
            }
        }
    }
}