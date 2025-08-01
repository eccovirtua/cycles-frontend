package com.example.cycles.repository

import com.example.cycles.data.RecommendRequest
import com.example.cycles.data.RecommendResponse
import com.example.cycles.network.RecsApiService
import javax.inject.Inject



class RecsRepository @Inject constructor(
    private val api: RecsApiService
) {
    suspend fun fetchRecs(itemId: String, topN: Int): RecommendResponse =
        api.getRecommendations(RecommendRequest(itemId, topN))


}