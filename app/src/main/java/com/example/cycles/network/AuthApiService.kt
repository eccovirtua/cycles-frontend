package com.example.cycles.network

import com.example.cycles.data.AuthenticationRequest
import com.example.cycles.data.AuthenticationResponse
import com.example.cycles.data.ForgotPasswordRequest
import com.example.cycles.data.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

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

}