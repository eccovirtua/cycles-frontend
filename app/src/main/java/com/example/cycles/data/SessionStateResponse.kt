package com.example.cycles.data

data class SessionStateResponse(
    val session_id: String,
    val domain: String,
    val last_item: RecommendationItem?,
    val iterations: Int,
    val limit: Int,
    val finished: Boolean
)