package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(): ViewModel() {

        private val _email = MutableStateFlow("")
        val email = _email.asStateFlow()

        private val _password = MutableStateFlow("")
        val password = _password.asStateFlow()

        // Estado expuesto al Composable (solo lectura boolean)
        private val _isLoading = MutableStateFlow(false)
        val isLoading = _isLoading.asStateFlow()

        private val _error = MutableStateFlow("")
        val error = _error.asStateFlow()

        fun onEmailChange(new: String) { _email.value = new}
        fun onPasswordChange(new:String) { _password.value = new}

        fun onRegisterClick() {
            viewModelScope.launch {
                _isLoading.value = true
                delay(150) //simula llamada
                // _error.value = "Fallo al autenticar" //simular el error
                _isLoading.value = false
                //si autentica: navController.navigate(Screen.Home.route)
            }
        }
}
