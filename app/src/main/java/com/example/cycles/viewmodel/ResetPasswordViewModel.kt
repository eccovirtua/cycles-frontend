package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.cycles.navigation.Screen
import com.example.cycles.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle


//actualmente, al cambiar la contraseña, esta nueva contraseña no pasa por ninguna validación de requerimientos mínimos
//o sea, ni de caracteres especiales ni de la longitud de la contraseña, la nueva contraseña puede quedar perfectamente "123"
@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repo: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val email: String = savedStateHandle["email"]
        ?: throw IllegalStateException("Email no encontrado en args")
    private val code: String = savedStateHandle["code"]
        ?: throw IllegalStateException("Code no encontrado en args")

    val pwd1 = MutableStateFlow("")
    val pwd2 = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun onPwd1(v: String) {
        pwd1.value = v
    }

    fun onPwd2(v: String) {
        pwd2.value = v
    }


    fun reset(nav: NavController) = viewModelScope.launch {
        if (pwd1.value != pwd2.value) {
            error.value = "Las contraseñas no coinciden"
            return@launch
        }
        isLoading.value = true
        val success = repo.resetPassword(email, code, newPassword = pwd1.value)
        isLoading.value = false
        if (success) {
            nav.navigate(Screen.Login.route) {
                popUpTo(0)
            }
        } else {
            error.value = "No se pudo actualizar la contraseña"
        }
    }
}
