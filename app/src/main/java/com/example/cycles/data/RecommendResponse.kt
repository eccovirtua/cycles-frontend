package com.example.cycles.data

import com.google.gson.annotations.SerializedName

data class RecommendResponse(
    @SerializedName("item_id") val itemId: Int,
    val recommendations: List<RecItem>
)