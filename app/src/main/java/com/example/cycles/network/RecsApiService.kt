package com.example.cycles.network

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.RecommendRequest
import com.example.cycles.data.RecommendResponse
import com.example.cycles.data.SeedResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path


interface RecsApiService {
    @POST("/recommend")
    suspend fun getRecommendations(
        @Body request: RecommendRequest,
        @Header("Authorization") token: String
    ): RecommendResponse


    @GET("/seed/{domain}")
    suspend fun getInitialSeed(
        @Path("domain") domain: String,
        @Header("Authorization") token: String

    ): SeedResponse

    @POST("/feedback/{domain}")
    suspend fun sendFeedback(
        @Path("domain") domain: String,
        @Body request: FeedbackRequest,
        @Header("Authorization") token: String
    ): SeedResponse
}