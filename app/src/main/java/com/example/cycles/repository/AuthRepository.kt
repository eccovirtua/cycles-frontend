package com.example.cycles.repository

import android.util.Log
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
            val response = apiService.getEmailByUsername(username)
                if (response.isSuccessful && response.body() != null) {
                    // Si conseguimos datos desde la api (python)
                    response.body()!!.email
                } else {
                    // Si python no consiguió nada (404 o 500)
                    Log.e("AuthRepo", "Error API: Código ${response.code()} - ${response.message()}")
                    null
                }
        } catch (e: Exception) {
            Log.e("AuthRepo", "Excepción de red: ${e.message}")
            null
        }
    }
}