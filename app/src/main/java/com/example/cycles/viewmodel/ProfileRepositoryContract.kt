package com.example.cycles.viewmodel

import android.net.Uri

// Data class required for ViewModel loading the photo
data class PhotoData(val profileImageUrl: String)

interface ProfileRepositoryContract {

    // 🛑 FUNCIÓN REQUERIDA: Carga solo los datos de la foto (incluyendo la URI local)
    suspend fun fetchProfilePhoto(userId: String): PhotoData

    // Sube la imagen y retorna la URL pública (AHORA URI LOCAL)
    suspend fun uploadImageAndGetUrl(userId: String, uri: Uri): String
}
