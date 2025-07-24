package com.example.cycles.network
//interfaz de Retrofit
import com.example.cycles.data.AuthenticationRequest
import com.example.cycles.data.AuthenticationResponse
import com.example.cycles.data.ForgotPasswordRequest
import com.example.cycles.data.RegisterRequest
import com.example.cycles.data.ResetPasswordRequest
import com.example.cycles.data.VerifyCodeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthenticationResponse


    @POST("api/auth/login")
    suspend fun login(@Body request: AuthenticationRequest): AuthenticationResponse

    @POST("/api/auth/forgot-password")
    @Headers("Content-Type: application/json")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<GenericResponse>
    
    @POST("api/auth/verify-code")
    suspend fun verifyCode(@Body request: VerifyCodeRequest): Response<GenericResponse>

    @PUT("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<GenericResponse>


    @POST("api/auth/check-username")
    suspend fun checkUsername(
        @Body body: Map<String,String>,
    ): Response<GenericResponse>

    @PATCH("api/auth/update-username")
    suspend fun updateUsername(
        @Body body: Map<String,String>,
        @Header("Authorization") auth: String
    ): Response<GenericResponse>


}