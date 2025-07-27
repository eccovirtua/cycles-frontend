package com.example.cycles.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cycles.data.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {

    // El Flow directo de DataStore, sin stateIn ni valor inicial fake
    val rawTokenFlow: Flow<String?> = userPreferences.token


    val tokenFlow: StateFlow<String?> = userPreferences.token
        .onEach { Log.d("AuthVM", "tokenFlow emits: $it") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000),null)
    //para saber si el token existe

    //exponer el token al resto de la interfaz con un stateflow llamando los metodos de userPreferences en un contexto viewModel
    suspend fun saveToken(token: String){
        userPreferences.saveToken(token) //aqui el token queda indefinidamente guardado y autenticado hasta que se llame a clearToken (logout)
    }

    //para cerrar sesión(limpiar el token)
    suspend fun logout(){
        Log.d("AuthVM", "Calling clearToken()")
        userPreferences.clearToken() // borra prefs[TOKEN_KEY]
    }
}

//ambos saveToken y logout tanto aquí como en UserPrefernces funcionan pero lo ideal es que
// todas las pantallas lean siempre de AuthViewModel.tokenFlow, y que solo a través de AuthViewModel se guarde/borre el token.
// Esto mantiene la lógica centralizada.
