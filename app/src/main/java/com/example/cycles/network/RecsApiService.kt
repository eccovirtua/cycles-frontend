package com.example.cycles.network

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.RecommendRequest
import com.example.cycles.data.RecommendResponse
import com.example.cycles.data.SeedResponse
import com.example.cycles.data.SessionCreateResponse
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


    @POST("reset/{domain}")
    suspend fun reset(
        @Path("domain") domain: String,
        @Header("Authorization") token: String
    ): SeedResponse


    // -------------------- NUEVOS ENDPOINTS DE SESIONES --------------------

    // Crear sesión
    @POST("/session/{domain}/create")
    suspend fun createSession(
        @Path("domain") domain: String,
        @Header("Authorization") token: String
    ): SessionCreateResponse

    // Enviar feedback en sesión
    @POST("/session/{session_id}/feedback")
    suspend fun sendSessionFeedback(
        @Path("session_id") sessionId: String,
        @Body request: FeedbackRequest,
        @Header("Authorization") token: String
    ): SeedResponse

    // Reset de sesión
    @POST("/session/{session_id}/reset")
    suspend fun resetSession(
        @Path("session_id") sessionId: String,
        @Header("Authorization") token: String
    ): SeedResponse

    // Finalizar sesión
    @POST("/session/{session_id}/finalize")
    suspend fun finalizeSession(
        @Path("session_id") sessionId: String,
        @Header("Authorization") token: String
    ): FinalListResponse

}






