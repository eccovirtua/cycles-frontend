package com.example.cycles.repository

import android.util.Log
import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.ItemAddRequest
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.data.ListCreateRequest
import com.example.cycles.data.ListUpdateRequest
import com.example.cycles.data.MovieSearchDto
import com.example.cycles.data.RecommendationItem
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.data.SessionStateResponse
import com.example.cycles.data.UserDashboardStats
import com.example.cycles.data.UserListBasic
import com.example.cycles.data.UserListDetail
import com.example.cycles.data.UserUsageStatus
import com.example.cycles.network.RecsApiService
import javax.inject.Inject




class RecsRepository @Inject constructor(
    private val api: RecsApiService
    // Ya no necesitamos UserPreferences aquí para el token
) {

    // NOTA: El AuthInterceptor inyectará el encabezado "Authorization: Bearer ..." automáticamente.

    suspend fun createSession(domain: String): SessionCreateResponse {
        return api.createSession(domain)
    }

    suspend fun sendFeedback(sessionId: String, itemId: String, feedback: Int): RecommendationItem {
        val req = FeedbackRequest(item_id = itemId, feedback = feedback)
        return api.sendSessionFeedback(sessionId, req).seed_item
    }

    suspend fun finalizeSession(sessionId: String): FinalListResponse {
        return api.finalizeSession(sessionId)
    }

    suspend fun getSessionState(sessionId: String): SessionStateResponse {
        return api.getSessionState(sessionId)
    }

    suspend fun getDashboardStats(): UserDashboardStats {
        return api.getUserDashboardStats()
    }

    suspend fun getItemDetails(itemId: String): ItemDetailResponse {
        return api.getItemDetails(itemId = itemId)
    }

    // --- Funciones de Listas ---

    suspend fun getMyLists(archived: Boolean? = null): List<UserListBasic> {
        return api.getMyLists(archived = archived)
    }

    suspend fun createList(name: String, icon: String, color: String): UserListBasic {
        val req = ListCreateRequest(name = name, iconName = icon, colorHex = color)
        return api.createList(request = req)
    }

    suspend fun addItemToList(listId: String, itemId: String): UserListBasic {
        val req = ItemAddRequest(itemId = itemId)
        return api.addItemToList(listId = listId, request = req)
    }

    suspend fun getListDetails(listId: String): UserListDetail {
        return api.getListDetails(listId = listId)
    }

    suspend fun updateList(listId: String, name: String, icon: String, color: String): UserListBasic {
        val req = ListUpdateRequest(name = name, iconName = icon, colorHex = color)
        return api.updateList(listId = listId, request = req)
    }

    suspend fun deleteList(listId: String) {
        val response = api.deleteList(listId = listId)
        if (!response.isSuccessful) {
            throw Exception("Error al borrar la lista: ${response.code()}")
        }
    }

    suspend fun removeItemFromList(listId: String, itemId: String): UserListBasic {
        return api.removeItemFromList(listId = listId, itemId = itemId)
    }

    suspend fun getUserUsage(): UserUsageStatus {
        return api.getUserUsage()
    }

    // --- Favoritos ---

    suspend fun addFavorite(itemId: String) {
        val response = api.addFavorite(itemId = itemId)
        if (!response.isSuccessful && response.code() != 201) {
            throw Exception("Error al añadir favorito: ${response.code()}")
        }
    }

    suspend fun removeFavorite(itemId: String) {
        val response = api.removeFavorite(itemId = itemId)
        if (!response.isSuccessful && response.code() != 204) {
            throw Exception("Error al quitar favorito: ${response.code()}")
        }
    }

    suspend fun getFavoriteStatus(itemId: String): Boolean {
        return try {
            api.getFavoriteStatus(itemId = itemId).isFavorite
        } catch (e: Exception) {
            Log.e("RecsRepository", "Error fetching favorite status for $itemId: ${e.message}")
            false
        }
    }

    suspend fun randomizeSeed(sessionId: String): RecommendationItem {
        return api.randomizeSeed(sessionId = sessionId).seed_item
    }

    suspend fun searchMovies(query: String): List<MovieSearchDto> {
        return api.searchMovies(query)
    }
}