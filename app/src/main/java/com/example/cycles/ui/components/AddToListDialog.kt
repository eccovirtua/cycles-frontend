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
import com.example.cycles.ui.screens.ListCreateDialog
import com.example.cycles.ui.screens.ListRowItem
import com.example.cycles.viewmodel.AddToListViewModel
import androidx.compose.runtime.*
import com.example.cycles.viewmodel.ListsViewModel

@Composable
fun AddToListDialog(
    itemIdToAdd: String,
    onDismiss: () -> Unit,
    // Inject both ViewModels
    addToListViewModel: AddToListViewModel = hiltViewModel(),
    listsViewModel: ListsViewModel = hiltViewModel() // <-- Inject ListsViewModel
) {
    val state by addToListViewModel.uiState.collectAsState()
    // State to control the create list dialog
    var showCreateListDialog by remember { mutableStateOf(false) }

    // --- Show Create List Dialog if needed ---
    if (showCreateListDialog) {
        ListCreateDialog(
            viewModel = listsViewModel, // Pass the injected ListsViewModel
            onDismiss = {
                showCreateListDialog = false
                // After creating, reload lists in the AddToListDialog
                addToListViewModel.loadLists()
            }
        )
    }

    // --- Main Add To List Dialog ---
    AlertDialog(
        onDismissRequest = {
            addToListViewModel.clearError()
            onDismiss()
        },
        title = { Text("Añadir a una lista") },
        text = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp, max = 300.dp)
            ) {
                // 🎯 Get local copies *after* null checks
                val currentSuccessMessage = state.successMessage
                val currentError = state.error

                when {
                    state.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    // 🎯 Use the local variable
                    currentSuccessMessage != null -> {
                        Text(
                            currentSuccessMessage, // Use local variable
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    // 🎯 Use the local variable
                    currentError != null -> {
                        Text(
                            currentError, // Use local variable
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    state.lists.isEmpty() && !state.isLoading -> {
                        Text(
                            "No tienes listas. Puedes crear una ahora.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    else -> {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(state.lists, key = { it.listId }) { list ->
                                ListRowItem(
                                    list = list,
                                    onClick = {
                                        addToListViewModel.addItemToList(list, itemIdToAdd, onComplete = onDismiss)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (state.lists.isEmpty() && !state.isLoading && state.successMessage == null && state.error == null) {
                Button(onClick = { showCreateListDialog = true }) {
                    Text("Crear")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = {
                addToListViewModel.clearError()
                onDismiss()
            }) {
                Text("Cerrar")
            }
        }
    )
}