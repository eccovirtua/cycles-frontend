package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject


@HiltViewModel
class WelcomeViewModel @Inject constructor(): ViewModel() {

    // Estado que indica si la pantalla está lista
    private val _isScreenReady = MutableStateFlow(false)
    val isScreenReady: StateFlow<Boolean> = _isScreenReady

    // Mensaje de bienvenida, por ejemplo
    private val _welcomeMessage = MutableStateFlow("Cycles")
    val welcomeMessage: StateFlow<String> = _welcomeMessage

    init {
        // Simula una pequeña carga o inicialización
        _isScreenReady.value = true
    }
}