// app/src/main/java/com/example/machteacher/api/SosApi.kt
package com.example.machteacher.api

import com.example.machteacher.dto.SosAcceptRequestDto
import com.example.machteacher.dto.SosCreateRequestDto
import com.example.machteacher.dto.SosResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SosApi {

    // Alumno crea un SOS
    @POST("api/sos")
    suspend fun createSos(
        @Body request: SosCreateRequestDto
    ): SosResponseDto

    // Mentor ve todos los SOS activos (PENDING)
    @GET("api/sos/active")
    suspend fun getActiveSos(): List<SosResponseDto>

    // Mentor acepta un SOS concreto
    @POST("api/sos/{id}/accept")
    suspend fun acceptSos(
        @Path("id") sosId: Long,
        @Body request: SosAcceptRequestDto
    ): SosResponseDto

    // 🔍 Historial SOS por alumno (coincide con tu backend: /api/sos/my?studentId=...)
    @GET("api/sos/my")
    suspend fun getSosByStudent(
        @Query("studentId") studentId: Long
    ): List<SosResponseDto>

    // 🔍 Historial SOS por mentor (coincide con /api/sos/mentor?mentorId=...)
    @GET("api/sos/mentor")
    suspend fun getSosByMentor(
        @Query("mentorId") mentorId: Long
    ): List<SosResponseDto>
}
