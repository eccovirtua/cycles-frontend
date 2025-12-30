package com.example.cycles.data

import com.google.gson.annotations.SerializedName

// Enumeración para distinguir el tipo de contenido (si no existe ya)
enum class ItemType {
    BOOK, SONG, MOVIE
}

// Modelo para un resultado en la lista de búsqueda

// Modelo para la respuesta completa de la API de búsqueda

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
    @SerializedName("cover_image") val coverImageUrl: String? = null,
    @SerializedName("show_age") val showAge: Boolean? = true

)

// Lo que devuelve /search/movies
data class MovieSearchDto(
    @SerializedName("id") val id: Int, // tmdb_id
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double
)

// Lo que devuelve /movies/{id}
data class MovieDetailDto(
    @SerializedName("tmdb_id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("genres") val genres: List<String>,
    @SerializedName("runtime") val runtime: Int,
    @SerializedName("status") val status: String,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("vote_average") val voteAverage: Double
)

data class SearchResultItem(
    val itemId: String,      // ID único (ej: "550" para Fight Club)
    val title: String,       // Título principal
    val subtitle: String,    // Texto secundario (Año, Artista, Autor)
    val imageUrl: String?,   // URL completa de la imagen (o null)
    val type: String         // "movie", "book", "user" (Para saber qué icono mostrar o a dónde navegar)
)