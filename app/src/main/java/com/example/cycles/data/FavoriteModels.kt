package com.example.cycles.data

import com.google.gson.annotations.SerializedName

// Usamos SearchResultItem para mostrar favoritos, así que solo necesitamos el status response

data class FavoriteStatusResponse(
    @SerializedName("item_id") val itemId: String,
    @SerializedName("is_favorite") val isFavorite: Boolean
)