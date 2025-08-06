package com.example.cycles.data

data class FeedbackRequest(
    val item_id: String,
    val feedback: Int   // -1 o +1
)