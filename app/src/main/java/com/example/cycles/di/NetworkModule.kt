package com.example.cycles.di

import com.example.cycles.BuildConfig
import com.example.cycles.network.AuthApiService
import com.example.cycles.network.ProfileApiService
import com.example.cycles.network.RecsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor
    ): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

    // ------------------ Retrofit para AUTENTICACIÓN ------------------
    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.AUTH_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ------------------ Retrofit para RECOMENDACIONES ------------------
    @Provides
    @Singleton
    @Named("recs")
    fun provideRecsRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.RECS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ------------------ Servicios API ------------------

    @Provides
    @Singleton
    fun provideAuthApiService(
        @Named("auth") retrofit: Retrofit
    ): AuthApiService = retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideRecsApiService(
        @Named("recs") retrofit: Retrofit
    ): RecsApiService = retrofit.create(RecsApiService::class.java)


    // 🎯 NUEVO: Proveedor para el servicio de Perfil de Usuario
    @Provides
    @Singleton
    fun provideProfileApiService(
        @Named("auth") retrofit: Retrofit //
    ): ProfileApiService = retrofit.create(ProfileApiService::class.java)


}
