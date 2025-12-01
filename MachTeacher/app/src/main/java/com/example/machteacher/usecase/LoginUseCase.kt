package com.example.machteacher.usecase

import com.example.machteacher.dto.LoginRequest
import com.example.machteacher.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<Boolean> {
        return try {
            // ✅ Crea el objeto LoginRequest como espera el backend
            val request = LoginRequest(email = email, password = password)
            val response = repository.login(request)

            // ✅ Si no hay excepción, devolvemos success
            Result.success(true)
        } catch (e: Exception) {
            // ✅ Manejo limpio de errores
            Result.failure(e)
        }
    }
}


