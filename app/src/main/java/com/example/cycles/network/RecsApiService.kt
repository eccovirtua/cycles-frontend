package com.example.cycles.network

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.SeedResponse
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.data.SessionStateResponse
import com.example.cycles.data.UserDashboardStats
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.Path


interface RecsApiService {

    @POST("/session/{domain}/create")
    suspend fun createSession(
        @Path("domain") domain: String,
        @Header("Authorization") token: String
    ): SessionCreateResponse

    @POST("/session/{session_id}/feedback")
    suspend fun sendSessionFeedback(
        @Path("session_id") sessionId: String,
        @Body request: FeedbackRequest,
        @Header("Authorization") token: String
    ): SeedResponse

    @POST("/session/{session_id}/finalize")
    suspend fun finalizeSession(
        @Path("session_id") sessionId: String,
        @Header("Authorization") token: String
    ): FinalListResponse

    @GET("session/{session_id}")
    suspend fun getSessionState(
        @Path("session_id") sessionId: String,
        @Header("Authorization") token: String
    ): SessionStateResponse


    @GET("stats/dashboard")
    suspend fun getUserDashboardStats(
        @Header("Authorization") token: String
    ): UserDashboardStats
}






