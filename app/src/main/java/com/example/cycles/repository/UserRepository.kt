package com.example.cycles.repository

import com.example.cycles.data.UserDto
import com.example.cycles.network.RecsApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: RecsApiService
) {
    suspend fun getUserProfile(uid: String): Result<UserDto> {
        return try {
            val response = api.getUserProfile(uid)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}