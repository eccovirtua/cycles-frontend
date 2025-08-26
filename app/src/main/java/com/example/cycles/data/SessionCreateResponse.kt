package com.example.cycles.data

data class SessionCreateResponse(
    val session_id: String,
    val domain: String,
    val seed_item: RecommendationItem
)