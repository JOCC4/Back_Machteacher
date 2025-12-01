package com.example.machteacher.api

import com.example.machteacher.dto.AuthResponse
import com.example.machteacher.dto.LoginRequest
import com.example.machteacher.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    // REVERTIDO: Solo existe un endpoint de registro genérico
    @POST("/api/auth/register")
    suspend fun register(@Body req: RegisterRequest): AuthResponse
}
