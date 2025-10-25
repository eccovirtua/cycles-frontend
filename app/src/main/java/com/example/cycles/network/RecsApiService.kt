package com.example.cycles.network

import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.ItemAddRequest
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.data.ListCreateRequest
import com.example.cycles.data.ListUpdateRequest
import com.example.cycles.data.SearchResponse
import com.example.cycles.data.SeedResponse
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.data.SessionStateResponse
import com.example.cycles.data.UserDashboardStats
import com.example.cycles.data.UserListBasic
import com.example.cycles.data.UserListDetail
import com.example.cycles.data.UserUsageStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface RecsApiService {

    @POST("/session/{domain}/create")
    suspend fun createSession(
        @Path("domain") domain: String,
        @Header("Authorization") token: String
    ): SessionCreateResponse

    @POST("/session/{session_id}/feedback")
    suspend fun sendSessionFeedback(
        @Path("session_id") sessionId: String,
        @Body request: FeedbackRequest,
        @Header("Authorization") token: String
    ): SeedResponse

    @POST("/session/{session_id}/finalize")
    suspend fun finalizeSession(
        @Path("session_id") sessionId: String,
        @Header("Authorization") token: String
    ): FinalListResponse

    @GET("session/{session_id}")
    suspend fun getSessionState(
        @Path("session_id") sessionId: String,
        @Header("Authorization") token: String
    ): SessionStateResponse


    @GET("stats/dashboard")
    suspend fun getUserDashboardStats(
        @Header("Authorization") token: String
    ): UserDashboardStats

    @GET("search")
    suspend fun searchItems(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20, // Parámetro opcional
        @Header("Authorization") token: String
    ): SearchResponse // Devuelve la lista de resultados

    // ✨ NUEVO: Endpoint de Detalle de Ítem
    @GET("item/{item_id}")
    suspend fun getItemDetails(
        @Path("item_id") itemId: String,
        @Header("Authorization") token: String
    ): ItemDetailResponse // Devuelve los detalles de UN ítem

    // --- LISTS ---
    @POST("lists")
    suspend fun createList(
        @Body request: ListCreateRequest,
        @Header("Authorization") token: String
    ): UserListBasic

    @GET("lists")
    suspend fun getMyLists(
        @Header("Authorization") token: String
    ): List<UserListBasic>

    @POST("lists/{list_id}/items")
    suspend fun addItemToList(
        @Path("list_id") listId: String,
        @Body request: ItemAddRequest,
        @Header("Authorization") token: String
    ): UserListBasic

    @GET("lists/{list_id}")
    suspend fun getListDetails(
        @Path("list_id") listId: String,
        @Header("Authorization") token: String
    ): UserListDetail

    @PUT("lists/{list_id}")
    suspend fun updateList(
        @Path("list_id") listId: String,
        @Body request: ListUpdateRequest,
        @Header("Authorization") token: String
    ): UserListBasic

    @DELETE("lists/{list_id}")
    suspend fun deleteList(
        @Path("list_id") listId: String,
        @Header("Authorization") token: String
    ): Response<Unit> // Devuelve 204 No Content

    @DELETE("lists/{list_id}/items/{item_id}")
    suspend fun removeItemFromList(
        @Path("list_id") listId: String,
        @Path("item_id") itemId: String,
        @Header("Authorization") token: String
    ): UserListBasic

    @GET("user/usage")
    suspend fun getUserUsage(@Header("Authorization") token: String): UserUsageStatus
}






