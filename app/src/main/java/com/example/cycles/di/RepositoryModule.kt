package com.example.cycles.di

import com.example.cycles.data.SessionCacheContract
import com.example.cycles.data.SessionCacheImpl
import com.example.cycles.repository.ProfileRepository
import com.example.cycles.viewmodel.ProfileRepositoryContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    // Le dice a Hilt: "Cuando alguien pida ProfileRepositoryContract, dale ProfileRepository"
    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepository
    ): ProfileRepositoryContract

    // Aprovechamos de prevenir el siguiente error:
    // Seguramente te pasará lo mismo con SessionCacheContract, así que lo arreglamos de una vez.
    @Binds
    @Singleton
    abstract fun bindSessionCache(
        sessionCacheImpl: SessionCacheImpl
    ): SessionCacheContract
}