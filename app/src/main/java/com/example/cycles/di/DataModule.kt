package com.example.cycles.di

import com.example.cycles.data.SessionCacheContract
import com.example.cycles.data.SessionCacheImpl
import com.example.cycles.viewmodel.ProfileRepositoryContract
import com.example.cycles.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * Vincula la implementación ProfileRepository a su contrato.
     * Esto permite inyectar ProfileRepositoryContract en el ViewModel.
     */
    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepository: ProfileRepository
    ): ProfileRepositoryContract


    /**
     * Vincula la implementación SessionCacheImpl a SessionCacheContract.
     * Esto le permite al ViewModel inyectar SessionCacheContract.
     */
    @Singleton
    @Binds
    abstract fun bindSessionCache(
        sessionCacheImpl: SessionCacheImpl
    ): SessionCacheContract
}
