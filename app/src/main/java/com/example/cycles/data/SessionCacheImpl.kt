//package com.example.cycles.data
//
//import android.content.Context
//import androidx.core.content.edit
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class SessionCacheImpl @Inject constructor(
//    @ApplicationContext context: Context
//) : SessionCacheContract {
//
//    private val PREFS_NAME = "CyclesSessionPrefs"
//
//    private val KEY_NAME = "profile_name"
//    private val KEY_BIO = "profile_bio"
//
//    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//
//    // 🛑 CORRECCIÓN CLAVE: Usamos commit = true para forzar la escritura SÍNCRONA.
//    // Esto asegura que los datos de Nombre y Bio estén disponibles inmediatamente
//    // cuando el ViewModel llama a getLocalName/getLocalBio.
//    override fun saveProfileMetadata(name: String, bio: String) {
//        sharedPrefs.edit(commit = true) {
//            putString(KEY_NAME, name)
//            putString(KEY_BIO, bio)
//        }
//    }
//
//    override fun getLocalName(): String? {
//        return sharedPrefs.getString(KEY_NAME, null)
//    }
//
//    override fun getLocalBio(): String? {
//        return sharedPrefs.getString(KEY_BIO, null)
//    }
//    override fun clearProfileMetadata() {
//        sharedPrefs.edit(commit = true) { // commit=true para asegurar escritura síncrona
//            remove(KEY_NAME)
//            remove(KEY_BIO)
//        }
//    }
//}
