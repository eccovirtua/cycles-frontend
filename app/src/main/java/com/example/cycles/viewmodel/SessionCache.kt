package com.example.cycles.viewmodel

import android.content.Context
//import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "session_cache")

object SessionCache {
    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    suspend fun saveSession(domain: String, sessionId: String) {
        val key = stringPreferencesKey(domain)
        appContext.dataStore.edit { prefs ->
            prefs[key] = sessionId
        }
    }

    suspend fun getSession(domain: String): String? {
        val key = stringPreferencesKey(domain)
        val prefs = appContext.dataStore.data.first()
        return prefs[key]
    }

    suspend fun clearSession(domain: String) {
        val key = stringPreferencesKey(domain)
        appContext.dataStore.edit { prefs ->
            prefs.remove(key)
        }
    }
}