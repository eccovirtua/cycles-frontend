package com.example.cycles.network

import com.example.cycles.data.RecommendRequest
import com.example.cycles.data.RecommendResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RecsApiService {
    @POST("recommend")
    suspend fun getRecommendations(
        @Body request: RecommendRequest
    ): RecommendResponse
}