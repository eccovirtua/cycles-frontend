package com.example.cycles.data


import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

//flujo de funcionamiento
//
//El backend devuelve response.jwtToken (un String).
//al hacer login en loginviewmodel se guarda el token = saveToken(jwtToken) entonces  DataStore lo guarda bajo "auth_token".
//UserPreferences.token: Flow<String?> lee ese valor cuando cambie.
//AuthViewModel.tokenFlow: StateFlow<String?> lo mantiene en memoria para Compose.
//La UI hace collectAsState() sobre tokenFlow y muestra Home o Login según sea null o no.
//clearToken() elimina la key, provocando una nueva emisión de null y forzando el logout en UI.

// Crear el DataStore
val Context.dataStore by preferencesDataStore(name = "user_prefs")


@Singleton
class UserPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    companion object {
        val TOKEN_KEY = stringPreferencesKey("auth_token")


    }

    //leer el token
    //Este Flow<String?> emite cada vez que cambie el contenido de prefs[TOKEN_KEY].
    val token: Flow<String?> = dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]
    }

    // aquí es donde realmente se pasa el token JWT como un string (token: String y desde del LoginViewModel o  AuthViewModel creo) y ese string
    //entra en el dataStore como un valor PERSISTENTE
    suspend fun saveToken(token: String) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // Borrar token (logout)
    //"detrás de escenas" del borrado de del token (clearToken)
    //provocando que el Flow en userPreferences.token emita null y, por tanto,
    // el tokenFlow también pase a null. Y la UI al recolectar ese null reacciona tirando al login
    suspend fun clearToken() {
        dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }


    //userPreferences.clearToken() elimina el valor del token de DataStore.
    //AuthViewModel.tokenFlow (que estaba observando userPreferences.token) emitirá null.
    //Cualquier pantalla que esté observando isTokenValid o directamente tokenFlow reaccionará al cambio, por ejemplo redirigiendo al usuario al login/Welcome de forma automática.
}