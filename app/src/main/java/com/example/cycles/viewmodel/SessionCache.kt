package com.example.cycles.viewmodel

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// 🎯 NUEVAS CLAVES PARA EL PERFIL LOCAL
private val USER_NAME_KEY = stringPreferencesKey("user_profile_name")
private val USER_BIO_KEY = stringPreferencesKey("user_profile_bio")

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

    // --- MÉTODOS DE PERFIL (NOMBRE Y BIO) ---

    // 🎯 Guardar nombre y biografía localmente
    suspend fun saveProfileMetadata(name: String, bio: String) {
        appContext.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
            prefs[USER_BIO_KEY] = bio
        }
    }

    // 🎯 Obtener nombre guardado
    suspend fun getLocalName(): String? {
        return appContext.dataStore.data
            .map { prefs -> prefs[USER_NAME_KEY] }
            .first()
    }

    // 🎯 Obtener biografía guardada
    suspend fun getLocalBio(): String? {
        return appContext.dataStore.data
            .map { prefs -> prefs[USER_BIO_KEY] }
            .first()
    }

    // 🎯 Limpiar Nombre y Bio (Al cerrar sesión, por ejemplo)
    suspend fun clearProfileMetadata() {
        appContext.dataStore.edit { prefs ->
            prefs.remove(USER_NAME_KEY)
            prefs.remove(USER_BIO_KEY)
        }
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
        // También limpiamos los metadatos de perfil al limpiar la sesión
        clearProfileMetadata()
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
