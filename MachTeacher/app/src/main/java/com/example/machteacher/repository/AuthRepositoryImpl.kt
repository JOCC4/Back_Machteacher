package com.example.machteacher.repository

import com.example.machteacher.api.AuthApi
import com.example.machteacher.dto.AuthResponse
import com.example.machteacher.dto.LoginRequest
import com.example.machteacher.dto.RegisterRequest
import com.example.machteacher.storage.AppDataStore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val dataStore: AppDataStore
) : AuthRepository {

    override suspend fun login(body: LoginRequest): AuthResponse {
        val response = api.login(body)

        // 🔹 Guardamos token, id y rol
        dataStore.saveToken(response.token)
        dataStore.saveUserId(response.id)
        dataStore.saveUserRole(response.role)   // << IMPORTANTE

        return response
    }

    override suspend fun register(body: RegisterRequest): AuthResponse {
        val response = api.register(body)

        // 🔹 Guardamos token, id y rol también al registrarse
        dataStore.saveToken(response.token)
        dataStore.saveUserId(response.id)
        dataStore.saveUserRole(response.role)   // << IMPORTANTE

        return response
    }
}
