package com.example.cycles.data
import com.google.gson.annotations.SerializedName

data class RecItem(
    @SerializedName("item_id") val itemId: Int,
    val title: String,
    val distance: Float
)