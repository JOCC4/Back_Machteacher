package com.example.machteacher.repository

import com.example.machteacher.dto.AuthResponse
import com.example.machteacher.dto.LoginRequest
import com.example.machteacher.dto.RegisterRequest

interface AuthRepository {
    suspend fun login(body: LoginRequest): AuthResponse
    suspend fun register(body: RegisterRequest): AuthResponse
}
