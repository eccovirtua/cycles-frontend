package com.example.cycles.repository

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.RecommendationItem
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.data.SessionStateResponse
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

    suspend fun createSession(domain: String): SessionCreateResponse {
        val token = bearer()
        return api.createSession(domain, token) // usa el endpoint nuevo
    }

    suspend fun sendFeedback(sessionId: String, itemId: String, feedback: Int): RecommendationItem? {
        val token = bearer()
        val req = FeedbackRequest(item_id = itemId, feedback = feedback)
        return api.sendSessionFeedback(sessionId, req, token).seed_item
    }

    suspend fun finalizeSession(sessionId: String): FinalListResponse {
        val token = bearer()
        return api.finalizeSession(sessionId, token)
    }

    suspend fun getSessionState(sessionId: String): SessionStateResponse {
        val token = bearer()
        return api.getSessionState(sessionId, token)
    }

}