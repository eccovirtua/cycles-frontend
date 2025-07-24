package com.example.cycles.repository


import android.util.Log
import com.example.cycles.data.AuthenticationRequest
import com.example.cycles.data.AuthenticationResponse
import com.example.cycles.data.ForgotPasswordRequest
import com.example.cycles.data.RegisterRequest
import com.example.cycles.data.ResetPasswordRequest
import com.example.cycles.data.VerifyCodeRequest
import com.example.cycles.network.AuthApiService
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApiService
) {
    suspend fun register(request: RegisterRequest): AuthenticationResponse {
        return api.register(request)
    }

    suspend fun login(request: AuthenticationRequest): AuthenticationResponse {
        return api.login(request)
    }

    suspend fun sendPasswordRecoveryEmail(email: String): String {
        val request = ForgotPasswordRequest(email)
        val response = api.forgotPassword(request)
        return if (response.isSuccessful) {
            response.body()?.message ?: "Código enviado"
        } else {
            "No se pudo enviar el correo"
        }
    }

    suspend fun verifyCode(email: String, code: String): Boolean {
        val response = api.verifyCode(VerifyCodeRequest(email, code))
        return response.isSuccessful && response.body()?.message == "Código válido"
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String): Boolean {
        val request = ResetPasswordRequest(email, code, newPassword)
        val response = api.resetPassword(request)
        if (!response.isSuccessful) {
            Log.e("API_ERROR", "Error: ${response.code()} - ${response.errorBody()?.string()}")
        }
            return response.isSuccessful
    }

    suspend fun checkUsername(name: String): Boolean {

        val response = api.checkUsername(
            mapOf("name" to name))
        Log.d("CheckUsername", "HTTP ${response.code()} • body = ${response.body()} • error = ${response.errorBody()?.string()}")

        val msg = response.body()?.message
        Log.d("CheckUsername", "message raw = \"$msg\"")

        return response.isSuccessful && msg.equals("available", ignoreCase = true)
    }

    suspend fun updateUsername(name: String, token: String): Boolean {
        val response = api.updateUsername(mapOf("name" to name), "Bearer $token")
        return response.isSuccessful
    }



}