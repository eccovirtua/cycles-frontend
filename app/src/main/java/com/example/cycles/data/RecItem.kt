package com.example.cycles.data

import com.google.gson.annotations.SerializedName

data class RecommendationItem(
    @SerializedName("item_id")   val itemId: String,
    @SerializedName("title")     val title: String,
    @SerializedName("distance")  val distance: Double,
    @SerializedName("image_url") val imageUrl: String?
//    val domain: String
)