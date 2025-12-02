package com.example.machteacher.api

import com.example.machteacher.dto.SubjectDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApi {

    @GET("api/catalog/subjects")
    suspend fun listSubjects(): List<SubjectDto>


    @GET("api/catalog/subjects/search")
    suspend fun searchSubjects(@Query("q") q: String): List<SubjectDto>
}