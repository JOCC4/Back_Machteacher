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


data class SessionRequest(
    val mentorId: Long,
    val studentId: Long?,
    val subjectId: Long,
    val packageTypeId: Long?,
    val date: String,
    val startTime: String,
    val durationMinutes: Int,
    val modality: String,
    val notes: String?,
    val priceUsd: Double
)


data class SessionDto(
    val id: Long,
    val studentId: Long,
    val mentorId: Long,
    val subjectId: Long,
    val packageTypeId: Long?,
    val date: String,
    val startTime: String,
    val durationMinutes: Int,
    val modality: String,
    val status: String,
    val priceUsd: Double,
    val notes: String?
)