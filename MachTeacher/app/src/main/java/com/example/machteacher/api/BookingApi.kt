package com.example.machteacher.api

import retrofit2.http.*
import retrofit2.Response

// ---------- Retrofit interface ----------
interface BookingApi {

    @POST("api/sessions")
    suspend fun createSession(@Body req: SessionRequest): SessionDto

    @GET("api/sessions/student/{id}")
    suspend fun listByStudent(@Path("id") studentId: Long): List<SessionDto>

    @DELETE("api/sessions/{id}")
    suspend fun deleteSession(@Path("id") id: Long): Response<Unit>
}

// ---------- Request/Response models (alineados al backend) ----------
// POST body (equivalente a SessionCreateRequest del backend)
data class SessionRequest(
    val mentorId: Long,
    val studentId: Long?,          // ⚠️ el backend lo ignora y toma del token; puedes enviar null
    val subjectId: Long,
    val packageTypeId: Long?,      // null si sesión individual
    val date: String,              // "yyyy-MM-dd"
    val startTime: String,         // "HH:mm"
    val durationMinutes: Int,      // 60, 90, 120, etc.
    val modality: String,          // "ONLINE" | "PRESENCIAL" (uppercase)
    val notes: String?,            // null si vacío
    val priceUsd: Double
)

// Respuesta (equivalente a SessionDTO del backend)
data class SessionDto(
    val id: Long,
    val studentId: Long,
    val mentorId: Long,
    val subjectId: Long,
    val packageTypeId: Long?,
    val date: String,              // "yyyy-MM-dd"
    val startTime: String,         // "HH:mm"
    val durationMinutes: Int,
    val modality: String,          // "ONLINE" | "PRESENCIAL"
    val status: String,            // "SCHEDULED", etc.
    val priceUsd: Double,
    val notes: String?
)