package com.example.cycles.repository

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.ItemAddRequest
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.data.ListCreateRequest
import com.example.cycles.data.ListUpdateRequest
import com.example.cycles.data.RecommendationItem
import com.example.cycles.data.SearchResponse
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.data.SessionStateResponse
import com.example.cycles.data.UserDashboardStats
import com.example.cycles.data.UserListBasic
import com.example.cycles.data.UserListDetail
import com.example.cycles.data.UserPreferences
import com.example.cycles.data.UserUsageStatus
import com.example.cycles.network.RecsApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject




class RecsRepository @Inject constructor(
    private val api: RecsApiService,
    private val userPrefs: UserPreferences
) {
    private suspend fun bearer(): String =
        "Bearer ${userPrefs.token.first() ?: throw Exception("Token no disponible")}"

    suspend fun createSession(domain: String): SessionCreateResponse {
        val token = bearer()
        return api.createSession(domain, token)
    }

    suspend fun sendFeedback(sessionId: String, itemId: String, feedback: Int): RecommendationItem? {
        val token = bearer()
        val req = FeedbackRequest(item_id = itemId, feedback = feedback)
        return api.sendSessionFeedback(sessionId, req, token).seed_item
    }

    suspend fun finalizeSession(sessionId: String): FinalListResponse {
        val token = bearer()
        return api.finalizeSession(sessionId, token)
    }

    suspend fun getSessionState(sessionId: String): SessionStateResponse {
        val token = bearer()
        return api.getSessionState(sessionId, token)
    }

    suspend fun getDashboardStats(): UserDashboardStats {
        val token = bearer()
        return api.getUserDashboardStats(token)
    }

    //Función para buscar ítems
    suspend fun searchItems(query: String, limit: Int = 20): SearchResponse {
        val token = bearer()
        return api.searchItems(query = query, limit = limit, token = token)
    }

    //Función para obtener detalles de un ítem
    suspend fun getItemDetails(itemId: String): ItemDetailResponse {
        val token = bearer()
        return api.getItemDetails(itemId = itemId, token = token)
    }

    // --- Funciones de Listas ---

    suspend fun getMyLists(): List<UserListBasic> {
        val token = bearer()
        return api.getMyLists(token = token)
    }

    suspend fun createList(name: String, icon: String, color: String): UserListBasic {
        val req = ListCreateRequest(name = name, iconName = icon, colorHex = color)
        val token = bearer()
        return api.createList(request = req, token = token)
    }

    suspend fun addItemToList(listId: String, itemId: String): UserListBasic {
        val req = ItemAddRequest(itemId = itemId)
        val token = bearer()
        return api.addItemToList(listId = listId, request = req, token = token)
    }

    suspend fun getListDetails(listId: String): UserListDetail {
        val token = bearer()
        return api.getListDetails(listId = listId, token = token)
    }

    suspend fun updateList(listId: String, name: String, icon: String, color: String): UserListBasic {
        val req = ListUpdateRequest(name = name, iconName = icon, colorHex = color)
        val token = bearer()
        return api.updateList(listId = listId, request = req, token = token)
    }

    suspend fun deleteList(listId: String) {
        val token = bearer()
        val response = api.deleteList(listId = listId, token = token)
        if (!response.isSuccessful) {
            throw Exception("Error al borrar la lista: ${response.code()}")
        }
    }

    suspend fun removeItemFromList(listId: String, itemId: String): UserListBasic {
        val token = bearer()
        return api.removeItemFromList(listId = listId, itemId = itemId, token = token)
    }

    suspend fun getUserUsage(): UserUsageStatus {
        val token = bearer()
        return api.getUserUsage(token = token)
    }

}