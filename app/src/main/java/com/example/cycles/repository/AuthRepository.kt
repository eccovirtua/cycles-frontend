package com.example.cycles.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.cycles.R
import com.example.cycles.data.UserCreateRequest
import com.example.cycles.network.RecsApiService
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: RecsApiService,
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
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

    fun logout() {
        firebaseAuth.signOut()
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(context, gso)
            googleSignInClient.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
                    Log.e(
                        "AuthRepo",
                        "Error API: Código ${response.code()} - ${response.message()}"
                    )
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
                val response =
                    apiService.checkEmailAvailability(email) // Asume que devuelve { "available": true }
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
                    Log.e(
                        "AUTH_DEBUG",
                        "Error Backend: Código ${response.code()} - Body: $errorBody"
                    )

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
    suspend fun updateUserProfileAuth(displayName: String?, photoUrl: String?) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val builder = UserProfileChangeRequest.Builder()

            if (displayName != null) {
                builder.setDisplayName(displayName)
            }

            // Si photoUrl es null, no lo tocamos, o podríamos borrarlo si quisieras
            if (photoUrl != null) {
                builder.photoUri = photoUrl.toUri()
            }

            user.updateProfile(builder.build()).await()
        }
    }
    suspend fun uploadProfilePicture(uid: String, imageUri: Uri): String? {
        return try {
            Log.e("UPLOAD_DEBUG", "1. Iniciando subida para UID: $uid")

            val storageRef = storage.reference.child("profile_images/$uid.jpg")

            Log.e("UPLOAD_DEBUG", "3. Ejecutando putFile...")
            // AQUI ES DONDE PROBABLEMENTE FALLA POR PERMISOS
            storageRef.putFile(imageUri).await()

            Log.e("UPLOAD_DEBUG", "4. putFile completado.")
            val downloadUrl = storageRef.downloadUrl.await().toString()

            Log.e("UPLOAD_DEBUG", "6. URL obtenida: $downloadUrl")
            downloadUrl

        } catch (e: Exception) {
            // SI MONGO RECIBE NULL, ES PORQUE ENTRAS AQUI
            Log.e("UPLOAD_DEBUG", "!!! ERROR EN SUBIDA !!!: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}