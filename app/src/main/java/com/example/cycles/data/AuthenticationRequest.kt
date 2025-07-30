package com.example.cycles.data

data class AuthenticationRequest(
    val usernameOrEmail: String,
    val password: String
)