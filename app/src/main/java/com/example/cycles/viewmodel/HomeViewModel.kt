package com.example.cycles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.cycles.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    ): ViewModel() {

    private val _isLoading = MutableStateFlow(false)


    // Estado expuesto al Composable (solo lectura)
    val isLoading = _isLoading.asStateFlow()



    private val _error = MutableStateFlow("")
    val error = _error.asStateFlow()


    //flujo para borrar el token usando un logout en algun logoutclick
    fun onLogoutClick(navController: NavHostController, authViewModel: AuthViewModel) {
        viewModelScope.launch {
            //1. borrar el token de una vez ya que no se necesita obtenerlo primero; ya que este mét0d0 de ClearToken()
            //ya borra directamente lo que esté guardado en DataStore bajo la clave de TOKEN_KEY
            //entonces el AuthViewModel  emitirá un null y entonces en la interfaz se podrá reaccionar a este cambio
            authViewModel.logout()
            //2. usando el nombre de la ruta definido en Screen, navegar al welcome que es la pantalla
            //principal con los botones de login/register, ya que se hizo un logout xd
            navController.navigate(Screen.Welcome.route) { //Compose por defecto añade la ruta de Welcome encima de la 'pila' de pantallas actual.
                // o sea que si el usuario simplemente pulsa hacia atrás, podrá volver al home incluso si el botón que acaba de presionar es un logout.
                popUpTo(route = Screen.Home.route) {
                    inclusive = true
                } //el 'popUpTo' sirve para que antes de que la navegacion ocurra como tal visualmente, elimine toda la pila de pantallas hasta (pero sin borrar) la que se esta referenciando, en este caso Home
                //inclusive = true permite también borrar la misma pantalla Home. que en este caso es el objetivo ya que estamos haciendo un logout
            }
        }
    }
}


