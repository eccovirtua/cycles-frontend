package com.example.cycles.repository

import android.util.Log
import com.example.cycles.data.UserCreateRequest
import com.example.cycles.network.RecsApiService
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject




class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: RecsApiService
) {
    // Login con Email
    fun loginWithEmail(email: String, pass: String): Task<AuthResult> {
        return firebaseAuth.signInWithEmailAndPassword(email, pass)
    }

    // Registro con Email
    fun registerWithEmail(email: String, pass: String): Task<AuthResult> {
        return firebaseAuth.createUserWithEmailAndPassword(email, pass)
    }

    // Login con Google (Credencial ya obtenida en la UI)
    fun signInWithGoogle(credential: AuthCredential): Task<AuthResult> {
        return firebaseAuth.signInWithCredential(credential)
    }

    // Cerrar sesión
//    fun logout() {
//        firebaseAuth.signOut()
//    }
//
//    // Verificar si hay usuario logueado
//    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun sendPasswordResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }

    suspend fun getEmailFromUsername(username: String): String? {
        return try {
            // 1. Hacemos la llamada
            val response = apiService.getEmailByUsername(username)
            if (response.isSuccessful && response.body() != null) {
                // Usamos ?. por seguridad, aunque el check != null ya protege bastante
                response.body()?.email
            } else {
                Log.e("AuthRepo", "Error API: Código ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Excepción de red: ${e.message}")
            null
        }
    }
    suspend fun createUserBackend(user: UserCreateRequest): Boolean {
        return try {
            val response = apiService.createUser(user)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("AuthRepo", "Excepción de red: ${e.message}")
            false
        }
    }

    suspend fun checkEmailAvailability(email: String): Boolean {
        return try {
            val response = apiService.checkEmailAvailability(email) // Asume que devuelve { "available": true }
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.available
            } else {
                // Si falla el server, asumimos false por seguridad o manejas el error distinto
                false
            }
        } catch (_: Exception) {
            false
        }
    }
    suspend fun checkUsernameAvailability(username: String): Boolean {
        return try {
            val response = apiService.checkUsernameAvailability(username)
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.available
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    suspend fun checkUserExists(): Boolean {
        return try {
            // Log antes de llamar
            Log.d("AUTH_DEBUG", "Preguntando al backend si el usuario existe...")

            val response = apiService.checkUserExists()

            if (response.isSuccessful && response.body() != null) {
                val exists = response.body()!!.exists
                Log.d("AUTH_DEBUG", "Respuesta Backend: Exito. Existe = $exists")
                exists
            } else {
                // AQUÍ ESTÁ EL PROBLEMA: Si el server da error, devolvías false silenciosamente.
                // Ahora veremos por qué falla.
                val errorBody = response.errorBody()?.string()
                Log.e("AUTH_DEBUG", "Error Backend: Código ${response.code()} - Body: $errorBody")

                // Si el error es 401 (No autorizado), es culpa del Token/Interceptor.
                // Si es 500, es culpa de Python.
                false
            }
        } catch (e: Exception) {
            // Si no hay internet o la URL está mal, caemos aquí.
            Log.e("AUTH_DEBUG", "Excepción Red: ${e.localizedMessage}")
            e.printStackTrace()
            false
        }
    }
}