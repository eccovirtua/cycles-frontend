package com.example.cycles.data

import com.google.gson.annotations.SerializedName


data class TimeStats(
    @SerializedName("hours_interacting")
    val hoursInteracting: Float = 0.0f,
    @SerializedName("hours_from_final_recs")
    val hoursFromFinalRecs: Float = 0.0f
)

data class DomainStats(
    @SerializedName("total_sessions")
    val totalSessions: Int = 0,
    @SerializedName("finished_sessions")
    val finishedSessions: Int = 0,
    @SerializedName("total_items_shown")
    val totalItemsShown: Int = 0,
    @SerializedName("items_liked")
    val itemsLiked: Int = 0,
    @SerializedName("avg_quality_score")
    val avgQualityScore: Float = 0.0f,
    @SerializedName("items_rejected")
    val itemsRejected: Int = 0,
    @SerializedName("final_recs_generated")
    val finalRecsGenerated: Int = 0,
    @SerializedName("time_stats")
    val timeStats: TimeStats
)

data class UserDashboardStats(
    @SerializedName("total_sessions")
    val totalSessions: Int,
    @SerializedName("finished_sessions")
    val finishedSessions: Int,
    @SerializedName("total_items_interacted")
    val totalItemsInteracted: Int,
    @SerializedName("total_avg_quality_score")
    val totalAvgQualityScore: Float = 0.0f,
    @SerializedName("total_items_liked")
    val totalItemsLiked: Int,
    @SerializedName("total_items_rejected")
    val totalItemsRejected: Int,
    @SerializedName("total_final_recs_generated")
    val totalFinalRecsGenerated: Int,
    @SerializedName("total_time_stats")
    val totalTimeStats: TimeStats,
    @SerializedName("domain_stats")
    val domainStats: Map<String, DomainStats>
)