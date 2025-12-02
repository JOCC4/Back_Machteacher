package com.example.machteacher.auth


import com.example.machteacher.dto.StudentRegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistrationApi {
    @POST("api/register/student")
    suspend fun registerStudent(@Body req: StudentRegisterRequest): Response<Unit>
}
