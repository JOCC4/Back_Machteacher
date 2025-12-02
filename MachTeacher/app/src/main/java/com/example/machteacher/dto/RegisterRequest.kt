package com.example.machteacher.dto

data class RegisterRequest(

    val fullName: String,
    val email: String,
    val password: String,
    val role: String?,
    val phone: String? = null,
    val city: String? = null,
    val country: String? = null,
    // comunes
    val university: String? = null,
    val career: String? = null,
    val semester: String? = null,
    // mentor
    val bio: String? = null,
    val subjects: String? = null,
    val hourlyRate: String? = null,
    val teachingExperience: String? = null,
    val aboutMe: String? = null,
    val references: String? = null
)

