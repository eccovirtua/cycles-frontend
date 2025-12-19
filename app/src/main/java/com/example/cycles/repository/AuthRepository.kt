package com.example.cycles.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import jakarta.inject.Inject



class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
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
    fun logout() {
        firebaseAuth.signOut()
    }

    // Verificar si hay usuario logueado
    fun isUserLoggedIn(): Boolean = firebaseAuth.currentUser != null

    fun sendPasswordResetEmail(email: String): Task<Void> {
        return firebaseAuth.sendPasswordResetEmail(email)
    }
}