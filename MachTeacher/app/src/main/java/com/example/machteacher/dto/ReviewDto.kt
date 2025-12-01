package com.example.machteacher.dto

import kotlinx.serialization.Serializable

@Serializable
data class ReviewDto(
    val author: String,
    val text: String,
    val rating: Double,
    val date: String // Idealmente, usar un tipo de fecha como kotlinx-datetime en el futuro
)
