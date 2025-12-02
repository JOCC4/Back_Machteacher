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


    @POST("api/sos")
    suspend fun createSos(
        @Body request: SosCreateRequestDto
    ): SosResponseDto


    @GET("api/sos/active")
    suspend fun getActiveSos(): List<SosResponseDto>


    @POST("api/sos/{id}/accept")
    suspend fun acceptSos(
        @Path("id") sosId: Long,
        @Body request: SosAcceptRequestDto
    ): SosResponseDto


    @GET("api/sos/my")
    suspend fun getSosByStudent(
        @Query("studentId") studentId: Long
    ): List<SosResponseDto>


    @GET("api/sos/mentor")
    suspend fun getSosByMentor(
        @Query("mentorId") mentorId: Long
    ): List<SosResponseDto>
}
