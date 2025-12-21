package com.example.cycles.data

import com.google.gson.annotations.SerializedName


data class ListCreateRequest(
    val name: String,
    @SerializedName("icon_name") val iconName: String,
    @SerializedName("color_hex") val colorHex: String
)

// Lo que enviamos para actualizar una lista
data class ListUpdateRequest(
    val name: String,
    @SerializedName("icon_name") val iconName: String,
    @SerializedName("color_hex") val colorHex: String
)

// Lo que enviamos para añadir un item
data class ItemAddRequest(
    @SerializedName("item_id") val itemId: String
)


// --- Modelos para Respuestas (Responses) ---

// La respuesta que recibimos (metadata de la lista)
data class UserListBasic(
    @SerializedName("list_id") val listId: String,
    val name: String,
    @SerializedName("icon_name") val iconName: String,
    @SerializedName("color_hex") val colorHex: String,
    @SerializedName("item_count") val itemCount: Int,
    @SerializedName("is_archived") val isArchived: Boolean
)

// La respuesta para el detalle de la lista
data class UserListDetail(
    @SerializedName("list_id") val listId: String,
    val name: String,
    @SerializedName("icon_name") val iconName: String,
    @SerializedName("color_hex") val colorHex: String,
    @SerializedName("item_count") val itemCount: Int,
    @SerializedName("is_archived") val isArchived: Boolean,
    val items: List<SearchResultItem> // Reutilizamos el modelo de búsqueda
)