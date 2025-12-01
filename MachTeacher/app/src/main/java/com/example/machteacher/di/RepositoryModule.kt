package com.example.machteacher.di

import com.example.machteacher.api.AuthApi
import com.example.machteacher.api.ProfileApi
import com.example.machteacher.api.SessionApi
import com.example.machteacher.api.CatalogApi
import com.example.machteacher.api.MessagingApi
import com.example.machteacher.api.SosApi                               // ⭐ NUEVO

import com.example.machteacher.dao.SessionDao
import com.example.machteacher.repository.AuthRepository
import com.example.machteacher.repository.AuthRepositoryImpl
import com.example.machteacher.repository.MessagingRepository
import com.example.machteacher.repository.MessagingRepositoryImpl
import com.example.machteacher.repository.ProfileRepository
import com.example.machteacher.repository.ProfileRepositoryImpl
import com.example.machteacher.repository.SessionRepository
import com.example.machteacher.repository.SessionRepositoryImpl

import com.example.machteacher.repository.SosRepository                    // ⭐ NUEVO
import com.example.machteacher.repository.SosRepositoryImpl               // ⭐ NUEVO

import com.example.machteacher.storage.AppDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        dataStore: AppDataStore
    ): AuthRepository =
        AuthRepositoryImpl(api, dataStore)

    @Provides
    @Singleton
    fun provideProfileRepository(
        api: ProfileApi,
        catalogApi: CatalogApi
    ): ProfileRepository =
        ProfileRepositoryImpl(api, catalogApi)

    @Provides
    @Singleton
    fun provideSessionRepository(
        dao: SessionDao,
        api: SessionApi,
        messagingApi: MessagingApi,
        dataStore: AppDataStore,
        catalogApi: CatalogApi,
        profileRepository: ProfileRepository
    ): SessionRepository =
        SessionRepositoryImpl(
            dao = dao,
            api = api,
            messagingApi = messagingApi,
            dataStore = dataStore,
            catalogApi = catalogApi,
            profileRepository = profileRepository
        )

    @Provides
    @Singleton
    fun provideMessagingRepository(
        api: MessagingApi
    ): MessagingRepository =
        MessagingRepositoryImpl(api)

    // ⭐⭐⭐⭐ NUEVO: REGISTRAR SOS ⭐⭐⭐⭐
    @Provides
    @Singleton
    fun provideSosRepository(
        api: SosApi
    ): SosRepository =
        SosRepositoryImpl(api)
}
