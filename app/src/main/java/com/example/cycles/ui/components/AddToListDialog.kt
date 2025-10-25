package com.example.cycles.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cycles.ui.screens.ListRowItem
import com.example.cycles.viewmodel.AddToListViewModel

@Composable
fun AddToListDialog(
    itemIdToAdd: String,
    onDismiss: () -> Unit,
    viewModel: AddToListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    AlertDialog(
        onDismissRequest = {
            viewModel.clearError() // Limpia el error al cerrar
            onDismiss()
        },
        title = { Text("Añadir a una lista") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 300.dp) // Damos altura al diálogo
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    state.successMessage != null -> {
                        Text(
                            state.successMessage!!,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    state.error != null -> {
                        Text(
                            state.error!!,
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    state.lists.isEmpty() -> {
                        Text(
                            "No tienes listas. Ve a la pestaña 'Listas' para crear una.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(state.lists, key = { it.listId }) { list ->
                                // Reutilizamos el Composable de ListsScreen
                                ListRowItem(
                                    list = list,
                                    onClick = {
                                        viewModel.addItemToList(list, itemIdToAdd, onComplete = onDismiss)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}