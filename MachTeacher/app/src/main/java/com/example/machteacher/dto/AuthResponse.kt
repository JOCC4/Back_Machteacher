package com.example.machteacher.dto


data class AuthResponse(
    val token: String,
    val id: Long,
    val fullName: String,
    val email: String,
    val role: String,
    val phone: String?,
    val city: String?,
    val country: String?,
    val university: String?,
    val career: String?,
    val semester: String?
)
