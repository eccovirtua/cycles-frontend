package com.example.cycles.network

import com.example.cycles.data.FavoriteStatusResponse
import com.example.cycles.data.FeedbackRequest
import com.example.cycles.data.FinalListResponse
import com.example.cycles.data.ItemAddRequest
import com.example.cycles.data.ItemDetailResponse
import com.example.cycles.data.ListCreateRequest
import com.example.cycles.data.ListUpdateRequest
import com.example.cycles.data.SearchResponse
import com.example.cycles.data.SearchResultItem
import com.example.cycles.data.SeedResponse
import com.example.cycles.data.SessionCreateResponse
import com.example.cycles.data.SessionStateResponse
import com.example.cycles.data.UserCreateRequest
import com.example.cycles.data.UserDashboardStats
import com.example.cycles.data.UserListBasic
import com.example.cycles.data.UserListDetail
import com.example.cycles.data.UserLookupResponse
import com.example.cycles.data.UserUsageStatus
import com.example.cycles.data.AvailabilityResponse
import com.example.cycles.data.UserDto
import com.example.cycles.data.UserExistsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface RecsApiService {

    @POST("/session/{domain}/create")
    suspend fun createSession(
        @Path("domain") domain: String
    ): SessionCreateResponse

    @POST("/session/{session_id}/feedback")
    suspend fun sendSessionFeedback(
        @Path("session_id") sessionId: String,
        @Body request: FeedbackRequest
    ): SeedResponse

    @POST("/session/{session_id}/finalize")
    suspend fun finalizeSession(
        @Path("session_id") sessionId: String
    ): FinalListResponse

    @GET("session/{session_id}")
    suspend fun getSessionState(
        @Path("session_id") sessionId: String
    ): SessionStateResponse


    @GET("stats/dashboard")
    suspend fun getUserDashboardStats(
    ): UserDashboardStats

    @GET("search")
    suspend fun searchItems(
        @Query("query") query: String,
        @Query("limit") limit: Int = 20 // Parámetro opcional
    ): SearchResponse // Devuelve la lista de resultados

    // ✨ NUEVO: Endpoint de Detalle de Ítem
    @GET("item/{item_id}")
    suspend fun getItemDetails(
        @Path("item_id") itemId: String
    ): ItemDetailResponse // Devuelve los detalles de UN ítem

    // --- LISTS ---
    @POST("lists")
    suspend fun createList(
        @Body request: ListCreateRequest
    ): UserListBasic

    @GET("lists")
    suspend fun getMyLists(
        @Query("archived") archived: Boolean?
    ): List<UserListBasic>

    @POST("lists/{list_id}/items")
    suspend fun addItemToList(
        @Path("list_id") listId: String,
        @Body request: ItemAddRequest
    ): UserListBasic

    @GET("lists/{list_id}")
    suspend fun getListDetails(
        @Path("list_id") listId: String
    ): UserListDetail

    @PUT("lists/{list_id}")
    suspend fun updateList(
        @Path("list_id") listId: String,
        @Body request: ListUpdateRequest
    ): UserListBasic

    @DELETE("lists/{list_id}")
    suspend fun deleteList(
        @Path("list_id") listId: String
    ): Response<Unit> // Devuelve 204 No Content

    @DELETE("lists/{list_id}/items/{item_id}")
    suspend fun removeItemFromList(
        @Path("list_id") listId: String,
        @Path("item_id") itemId: String
    ): UserListBasic

    @PUT("lists/{list_id}/archive")
    suspend fun archiveList(
        @Path("list_id") listId: String
    ): UserListBasic // Devuelve la lista actualizada

    @PUT("lists/{list_id}/unarchive")
    suspend fun unarchiveList(
        @Path("list_id") listId: String
    ): UserListBasic // Devuelve la lista actualizada

    @POST("favorites/{item_id}")
    suspend fun addFavorite(
        @Path("item_id") itemId: String
    ): Response<Unit> // Devuelve 201 Created sin cuerpo útil

    @DELETE("favorites/{item_id}")
    suspend fun removeFavorite(
        @Path("item_id") itemId: String
    ): Response<Unit> // Devuelve 204 No Content

    @GET("favorites")
    suspend fun getFavorites(
    ): List<SearchResultItem>

    @GET("favorites/status/{item_id}")
    suspend fun getFavoriteStatus(
        @Path("item_id") itemId: String
    ): FavoriteStatusResponse
    @GET("user/usage")
    suspend fun getUserUsage(
    ):
    UserUsageStatus

    @POST("session/{session_id}/randomize")
    suspend fun randomizeSeed(
        @Path("session_id") sessionId: String
    ): SeedResponse // Devuelve el nuevo seed


    @GET("users/get-email/{username}")
    suspend fun getEmailByUsername(
        @Path("username") username: String
    ): Response<UserLookupResponse>

    @POST("users/create")
    suspend fun createUser(
        @Body user: UserCreateRequest
    ): Response<Unit>

    // Verificar Email
    @GET("users/check-email/{email}")
    suspend fun checkEmailAvailability(
        @Path("email") email: String
    ): Response<AvailabilityResponse>

    // Verificar Username
    @GET("users/check-username/{username}")
    suspend fun checkUsernameAvailability(
        @Path("username") username: String
    ): Response<AvailabilityResponse>

    @GET("users/me/exists")
    suspend fun checkUserExists(): Response<UserExistsResponse>

    @GET("/users/{uid}")
    suspend fun getUserProfile(@Path("uid") uid: String): Response<UserDto>
}







