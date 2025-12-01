package com.example.machteacher.api

import com.example.machteacher.dto.RatingRequest
import com.example.machteacher.dto.SessionDto
import com.example.machteacher.dto.SessionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface SessionApi {

    @POST("api/sessions")
    suspend fun createSession(
        @Body req: SessionRequest
    ): SessionDto

    @GET("api/sessions/student/{id}")
    suspend fun listByStudent(
        @Path("id") studentId: Long
    ): List<SessionDto>

    @GET("api/sessions/mentor/{id}")
    suspend fun listByMentor(
        @Path("id") mentorId: Long
    ): List<SessionDto>

    @DELETE("api/sessions/{id}")
    suspend fun deleteSession(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("api/sessions/{id}/rating")
    suspend fun rateSession(
        @Path("id") id: Long,
        @Body body: RatingRequest
    ): Response<Unit>
}
