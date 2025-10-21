package com.example.cycles.data

import com.google.gson.annotations.SerializedName

data class FinalListResponse(
    val recommendations: List<RecommendationItem>,

    @SerializedName("session_avg_quality")
    val sessionAvgQuality: Float = 0.0f
)
