package com.example.machteacher.dto

data class LoginResponse(
    val token: String,
    val fullName: String? = null
)
