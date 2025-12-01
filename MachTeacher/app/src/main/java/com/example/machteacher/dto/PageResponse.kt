package com.example.machteacher.dto

// Estructura genérica para respuestas paginadas del backend
data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val number: Int
)
