package com.example.cycles.data

import com.google.gson.annotations.SerializedName

// Enumeración para distinguir el tipo de contenido (si no existe ya)
enum class ItemType {
    BOOK, SONG, MOVIE
}

// Modelo para un resultado en la lista de búsqueda
data class SearchResultItem(
    @SerializedName("item_id")
    val itemId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("domain")
    val domain: String, // "movie", "book", "music"
    @SerializedName("image_url")
    val imageUrl: String?
)

// Modelo para la respuesta completa de la API de búsqueda
data class SearchResponse(
    @SerializedName("results")
    val results: List<SearchResultItem>
)

// Modelo para la respuesta detallada de un ítem (hereda de RecItem si es útil)
// O define los campos directamente como lo hace el backend
data class ItemDetailResponse(
    @SerializedName("item_id")
    val itemId: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("genres")
    val genres: List<String>?,
    @SerializedName("year")
    val year: String?,
    @SerializedName("artist") // Específico de música
    val artist: String?,
    @SerializedName("google_avg_rating") // Específico de libros
    val googleAvgRating: Float?,
    @SerializedName("imdb_score") // Específico de películas
    val imdbScore: Float?,
    @SerializedName("listeners") // Específico de música
    val listeners: Int?
    // Añade aquí cualquier otro campo que tu backend devuelva
)

data class UserDto(
    @SerializedName("firebaseUid") val firebaseUid: String,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String, // Esto lo usaremos como username
    @SerializedName("age") val age: Int,
    @SerializedName("profile_picture") val profilePictureUrl: String,

    // Campos OPCIONALES (Solo existen si el usuario editó perfil)
    @SerializedName("country") val country: String? = null,
    @SerializedName("cover_image") val coverImageUrl: String? = null

)