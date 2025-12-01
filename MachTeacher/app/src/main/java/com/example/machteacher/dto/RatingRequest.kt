package com.example.machteacher.dto

data class RatingRequest(
    val rating: Int,
    val comment: String? = null
)
