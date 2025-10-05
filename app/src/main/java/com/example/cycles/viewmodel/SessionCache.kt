package com.example.cycles.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


private val LAST_DOMAIN_KEY = stringPreferencesKey("last_domain")
private val Context.dataStore by preferencesDataStore(name = "session_cache")

object SessionCache {
    private lateinit var appContext: Context


    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun sessionKey(domain: String) = stringPreferencesKey("${domain}_session")
    private fun resetKey(domain: String) = booleanPreferencesKey("${domain}_reset")


    // 🔹 Guardar el dominio actualmente activo
    fun saveLastDomain(domain: String) {
        CoroutineScope(Dispatchers.IO).launch {
            appContext.dataStore.edit { prefs ->
                prefs[LAST_DOMAIN_KEY] = domain
            }
        }
    }

    // 🔹 Obtener el último dominio activo
    suspend fun getLastDomain(): String? {
        val prefs = appContext.dataStore.data.first()
        return prefs[LAST_DOMAIN_KEY]
    }


    // ✅ Guardar sesión (suspend para evitar race conditions)
    suspend fun saveSession(domain: String, sessionId: String) {
        appContext.dataStore.edit { prefs ->
            prefs[sessionKey(domain)] = sessionId
        }
    }

    // ✅ Obtener sesión guardada
    suspend fun getSession(domain: String): String? {
        val prefs = appContext.dataStore.data.first()
        return prefs[sessionKey(domain)]
    }

    // ✅ Eliminar sesión
    suspend fun clearSession(domain: String) {
        appContext.dataStore.edit { prefs ->
            prefs.remove(sessionKey(domain))
        }
    }

    // ✅ Flag de reinicio
    suspend fun isSessionReset(domain: String): Boolean {
        val prefs = appContext.dataStore.data.first()
        return prefs[resetKey(domain)] ?: false
    }

    suspend fun markSessionAsReset(domain: String) {
        appContext.dataStore.edit { prefs ->
            prefs[resetKey(domain)] = true
        }
    }

    suspend fun clearSessionResetFlag(domain: String) {
        appContext.dataStore.edit { prefs ->
            prefs.remove(resetKey(domain))
        }
    }

}
