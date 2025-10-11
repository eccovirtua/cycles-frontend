package com.example.cycles.repository

import android.content.Context
import android.net.Uri
import com.example.cycles.viewmodel.PhotoData
import com.example.cycles.viewmodel.ProfileRepositoryContract
import com.example.cycles.data.UserPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context, // NECESARIO para leer la URI
    private val userPreferences: UserPreferences,     // Para el Token (DataStore)
) : ProfileRepositoryContract {


    private suspend fun getAuthToken(): String? {
        // Leemos el token d e DataStore
        val token = userPreferences.token.first()
        return if (!token.isNullOrEmpty()) "Bearer $token" else null
    }

    override suspend fun fetchProfilePhoto(userId: String): PhotoData {
        // 1. Obtener el token (mantenemos la estructura del contrato)
        val authToken = getAuthToken()

        // 2. Leer la URI local de la foto guardada en UserPreferences
        // NOTA: Asumo que tienes una propiedad Flow<String?> llamada 'profilePhotoUri' en UserPreferences.
        val localPhotoUri = userPreferences.profilePhotoUri.first()

        // 3. Determinar qué URL devolver
        val imageUrl = if (!authToken.isNullOrEmpty() && !localPhotoUri.isNullOrEmpty()) {
            // Si el usuario está autenticado y tenemos un URI guardado, lo usamos.
            localPhotoUri
        } else {
            // Si no hay URI guardado o no hay token, usamos el placeholder.
            "https://placehold.co/200x200/cccccc/333333?text=SIN+FOTO"
        }

        return PhotoData(profileImageUrl = imageUrl)
    }
    override suspend fun uploadImageAndGetUrl(userId: String, uri: Uri): String {
        // 1. (Simulación): Verificación del token, esencial para el contrato.
        val authToken = getAuthToken()
        if (authToken.isNullOrEmpty()) {
            throw IOException("Authentication token missing. Cannot upload image.")
        }

        // 2. Copia la imagen de la URI temporal (galería) a un archivo permanente en la caché.
        val cacheUri = copyUriToCache(uri)

        // 3. Guarda el nuevo URI de la caché en las preferencias del usuario para persistencia.
        userPreferences.saveProfilePhotoUri(cacheUri.toString())

        // 4. Devuelve la URI de la caché, que es la "URL pública" local.
        return cacheUri.toString()
    }

    private fun copyUriToCache(sourceUri: Uri): Uri {
        val cacheFile = File(context.cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")

        val inputStream = context.contentResolver.openInputStream(sourceUri)
            ?: throw IOException("No se pudo abrir el InputStream para la URI de origen.")

        // 3. Abrir el OutputStream del archivo de destino (caché)
        val outputStream = FileOutputStream(cacheFile)

        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }

        return Uri.fromFile(cacheFile)

    }
}
