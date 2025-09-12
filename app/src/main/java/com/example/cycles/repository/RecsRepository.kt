package com.example.cycles.repository

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
//import com.example.cycles.data.RecommendRequest
//import com.example.cycles.data.RecommendResponse
import com.example.cycles.data.RecommendationItem
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.data.UserPreferences
import com.example.cycles.network.RecsApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject




class RecsRepository @Inject constructor(
    private val api: RecsApiService,
    private val userPrefs: UserPreferences
) {
    private suspend fun bearer(): String =
        "Bearer ${userPrefs.token.first() ?: throw Exception("Token no disponible")}"

    suspend fun getInitialSeed(domain: String): RecommendationItem? {
        val token = userPrefs.token.first() ?: throw Exception("Token no disponible")
        return api.getInitialSeed(domain, "Bearer $token").seed_item
    }
    /** Envía feedback y devuelve el siguiente seed; retorna null si ya no hay más seeds */
    suspend fun sendSessionFeedback(sessionId: String, itemId: String, feedback: Int): RecommendationItem? {
        val token = bearer()
        val req = FeedbackRequest(item_id = itemId, feedback = feedback)
        return api.sendSessionFeedback(sessionId, req, token).seed_item
    }

    /** Finaliza sesión y devuelve lista final de recomendaciones */
    suspend fun finalizeSession(sessionId: String): FinalListResponse {
        val token = bearer()
        return api.finalizeSession(sessionId, token)
    }

    suspend fun createSession(domain: String): SessionCreateResponse {
        val token = bearer()
        return api.createSession(domain, token)
    }

    suspend fun getFinalGridForDomain(domain: String): FinalListResponse? {
        return try {
            val token = bearer()
            api.getFinalGridForDomain(domain, token) // este metodo debe estar en tu RecsApiService
        } catch (_: Exception) {
            null // si no existe, devuelve null
        }
    }


}

