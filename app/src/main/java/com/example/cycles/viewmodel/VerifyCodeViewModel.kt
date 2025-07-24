package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
//import com.example.cycles.navigation.Screen
import com.example.cycles.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle
import java.net.URLEncoder

@HiltViewModel
class VerifyCodeViewModel @Inject constructor(
    private val repo: AuthRepository,
    savedStateHandle: SavedStateHandle       // ← Necesario para leer args
) : ViewModel() {

    // recuperar mail desde los nav args
    val email: String = savedStateHandle["email"]
        ?: throw IllegalStateException("Email no presente en argumentos")


    val code = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow<String?>(null)

    fun onCodeChange(v: String) { code.value = v }

    fun verify(nav: NavController) = viewModelScope.launch {
        isLoading.value = true
        val ok = repo.verifyCode(email, code.value)
        isLoading.value = false

        if (ok) {
            val encodedEmail = URLEncoder.encode(email, "UTF-8")
            nav.navigate("reset_password/$encodedEmail/${code.value}")
        }
        else {
            error.value = "Código inválido"
        }
    }
}
