package com.example.cycles.data
import com.google.gson.annotations.SerializedName

data class RecommendRequest(
    @SerializedName("item_id") val itemId: Int,
    @SerializedName("top_n")   val topN: Int = 5
)