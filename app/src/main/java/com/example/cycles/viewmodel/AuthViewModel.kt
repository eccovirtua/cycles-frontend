package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    val tokenFlow: StateFlow<String?> = userPreferences.token
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),null)
    //para saber si el token existe

    // Usuario autenticado si token != null
    val isTokenValid: StateFlow<Boolean> = tokenFlow
        .map { token -> !token.isNullOrEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    //se sugiere:
    //Para un chequeo real de expiración, reemplaza el .map { !token.isNullOrEmpty() } por tu función de decodificar y comparar exp.



    //exponer el token al resto de la interfaz con un stateflow llamando los metodos de userPreferences en un contexto viewModel
    suspend fun saveToken(token: String){
        userPreferences.saveToken(token) //aqui el token queda indefinidamente guardado y autenticado hasta que se llame a clearToken (logout)
    }

    //para cerrar sesión(limpiar el token)
    suspend fun logout(){
        userPreferences.clearToken() // borra prefs[TOKEN_KEY]
    }
}

//ambos saveToken y logout tanto aquí como en UserPrefernces funcionan pero lo ideal es que
// todas las pantallas lean siempre de AuthViewModel.tokenFlow, y que solo a través de AuthViewModel se guarde/borre el token.
// Esto mantiene la lógica centralizada.
