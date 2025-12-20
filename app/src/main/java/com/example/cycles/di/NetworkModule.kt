package com.example.cycles.di


import android.util.Log
import com.example.cycles.network.RecsApiService
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Interceptor para Firebase
    @Singleton
    @Provides
    fun provideAuthInterceptor(): Interceptor = Interceptor { chain ->
        val user = FirebaseAuth.getInstance().currentUser
        val requestBuilder = chain.request().newBuilder()

        if (user != null) {

            try {
                val tokenResult = Tasks.await(user.getIdToken(true))
                val token = tokenResult.token
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            } catch (e: Exception) {
                Log.e("AuthInterceptor", "Error obteniendo token", e)
            }
        }
        chain.proceed(requestBuilder.build())
    }

    // 3. Inyectar interceptor en OkHttpClient
    @Singleton
    @Provides
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    @Singleton
    @Provides
    fun provideRecsApiService(okHttpClient: OkHttpClient): RecsApiService {
        return Retrofit.Builder()
            .baseUrl("https://knn-ann-algorithm-377792293762.southamerica-west1.run.app")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecsApiService::class.java)
    }
    // Dentro de tu NetworkModule u otro módulo:
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
}