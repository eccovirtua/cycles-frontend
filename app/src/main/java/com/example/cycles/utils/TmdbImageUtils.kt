package com.example.cycles.utils

object TmdbImageUtils {
    private const val BASE_URL = "https://image.tmdb.org/t/p/"

    // Tamaños disponibles en TMDB: w92, w154, w185, w342, w500, w780, original

    // Para listas (Grid) - Ligero y rápido
    fun buildPosterUrl(path: String?, size: String = "w342"): String {
        return if (!path.isNullOrEmpty()) "$BASE_URL$size$path" else ""
    }

    // Para detalles (Pantalla completa) - Mejor calidad
    fun buildBackdropUrl(path: String?, size: String = "w780"): String {
        return if (!path.isNullOrEmpty()) "$BASE_URL$size$path" else ""
    }
}