package com.example.cycles.network

import com.example.cycles.viewmodel.PhotoData
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ProfileApiService {

    /**
     * Obtiene la URL de la foto de perfil del usuario autenticado.
     * Ruta esperada en el backend: GET /users/profile/photo
     */
    @GET("users/profile/photo")
    suspend fun fetchProfilePhotoUrl(
        @Header("Authorization") authToken: String
    ): PhotoData

    @Multipart
    @POST("users/profile/photo")
    suspend fun uploadProfilePhoto(
        @Header("Authorization") authToken: String,
        @Part photo: MultipartBody.Part // El nombre del campo debe ser 'photo'
    ): PhotoData
}
