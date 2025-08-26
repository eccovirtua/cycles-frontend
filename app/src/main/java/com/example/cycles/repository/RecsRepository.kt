package com.example.cycles.repository

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.RecommendRequest
import com.example.cycles.data.RecommendResponse
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


    suspend fun fetchRecs(itemId: String, topN: Int): RecommendResponse {
        val token = userPrefs.token.first() ?: throw Exception("Token no disponible")
        return api.getRecommendations(RecommendRequest(itemId, topN), "Bearer $token")
    }

    suspend fun getInitialSeed(domain: String): RecommendationItem {
        val token = userPrefs.token.first() ?: throw Exception("Token no disponible")
        return api.getInitialSeed(domain, "Bearer $token").seed_item
    }

    suspend fun sendFeedback(domain: String, itemId: String, feedback: Int): RecommendationItem {
        val request = FeedbackRequest(item_id = itemId, feedback = feedback)
        val resp  = api.sendFeedback(domain, request, bearer())
        return resp.seed_item
    }

    suspend fun reset(domain: String): RecommendationItem {
        val token = userPrefs.token.first() ?: throw Exception("Token no disponible")
        return api.reset(domain, "Bearer $token").seed_item
    }
    suspend fun createSession(domain: String): SessionCreateResponse {
        val token = bearer()
        return api.createSession(domain, token)
    }

    suspend fun sendSessionFeedback(sessionId: String, itemId: String, feedback: Int): RecommendationItem {
        val token = bearer()
        val req = FeedbackRequest(item_id = itemId, feedback = feedback) // usa item_id
        return api.sendSessionFeedback(sessionId, req, token).seed_item
    }

    suspend fun resetSession(sessionId: String): RecommendationItem {
        val token = bearer()
        return api.resetSession(sessionId, token).seed_item
    }

    suspend fun finalizeSession(sessionId: String): FinalListResponse {
        val token = bearer()
        return api.finalizeSession(sessionId, token)
    }
}
