package com.example.cycles.di


import android.util.Log
import com.example.cycles.network.RecsApiService
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Singleton
    @Provides
    fun provideAuthInterceptor(): Interceptor = Interceptor { chain ->
        val user = FirebaseAuth.getInstance().currentUser
        val requestBuilder = chain.request().newBuilder()

        if (user != null) {
            try {
                val tokenResult = Tasks.await(user.getIdToken(false))
                val token = tokenResult.token

                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                    Log.d("AuthInterceptor", "Token inyectado correctamente")
                }
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Error obteniendo token: ${e.message}")
            }
        } else {
            Log.w("AuthInterceptor", "Usuario no logueado, enviando sin token")
        }
        chain.proceed(requestBuilder.build())
    }

    // 2. Cliente HTTP
    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // Cloud Run a veces tarda en despertar
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun provideRecsApiService(okHttpClient: OkHttpClient): RecsApiService {
        return Retrofit.Builder()
//            .baseUrl("https://knn-ann-algorithm-377792293762.southamerica-west1.run.app")
            .baseUrl("http://10.0.2.2:8000/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

//    private const val BASE_URL = "http://10.0.2.2:8000/"
//    @Provides
//    @Singleton
//    fun provideRetrofit(): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }
//
//    @Provides
//    @Singleton
//    fun provideCyclesApi(retrofit: Retrofit): RecsApiService {
//        return retrofit.create(RecsApiService::class.java)
//    }
}