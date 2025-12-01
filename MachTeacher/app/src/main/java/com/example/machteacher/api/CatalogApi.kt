package com.example.machteacher.api

import com.example.machteacher.dto.SubjectDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CatalogApi {
    // Opción A: listado completo
    @GET("api/catalog/subjects")
    suspend fun listSubjects(): List<SubjectDto>

    // Opción B (si tu backend tiene búsqueda): /api/catalog/subjects/search?q=Matemáticas
    @GET("api/catalog/subjects/search")
    suspend fun searchSubjects(@Query("q") q: String): List<SubjectDto>
}