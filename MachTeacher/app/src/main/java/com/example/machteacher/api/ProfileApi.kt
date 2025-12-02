package com.example.machteacher.api

import com.example.machteacher.dto.PageResponse
import com.example.machteacher.dto.ProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProfileApi {

    // ---------- STUDENT ----------
    @GET("api/profiles/student/{userId}")
    suspend fun getStudentProfile(
        @Path("userId") userId: Long
    ): ProfileDto

    @PUT("api/profiles/student/{userId}")
    suspend fun updateStudentProfile(
        @Path("userId") userId: Long,
        @Body body: ProfileDto
    ): ProfileDto

    // ---------- MENTOR ----------
    @GET("api/profiles/mentor/{userId}")
    suspend fun getMentorProfile(
        @Path("userId") userId: Long
    ): ProfileDto

    @PUT("api/profiles/mentor/{userId}")
    suspend fun updateMentorProfile(
        @Path("userId") userId: Long,
        @Body body: ProfileDto
    ): ProfileDto


    @GET("api/profiles/{type}/{userId}")
    suspend fun getProfile(
        @Path("type") type: String,
        @Path("userId") userId: Long
    ): Response<ProfileDto>


    @GET("api/profiles/mentors")
    suspend fun getMentors(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("q") q: String? = null
    ): PageResponse<ProfileDto>
}
