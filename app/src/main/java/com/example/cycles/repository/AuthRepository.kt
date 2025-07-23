package com.example.cycles.repository


import com.example.cycles.data.AuthenticationRequest
import com.example.cycles.data.AuthenticationResponse
import com.example.cycles.data.RegisterRequest
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
}