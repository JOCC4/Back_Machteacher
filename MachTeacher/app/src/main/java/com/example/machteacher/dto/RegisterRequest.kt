package com.example.machteacher.dto

data class RegisterRequest(
    // básicos
    val fullName: String,
    val email: String,
    val password: String,
    val role: String?,            // "MENTOR" | "STUDENT" (si null, backend usa STUDENT)
    // contacto
    val phone: String? = null,
    val city: String? = null,
    val country: String? = null,
    // comunes
    val university: String? = null,
    val career: String? = null,
    val semester: String? = null,
    // mentor
    val bio: String? = null,
    val subjects: String? = null,           // "Mate,Física"
    val hourlyRate: String? = null,         // String en MVP
    val teachingExperience: String? = null,
    val aboutMe: String? = null,
    val references: String? = null
)

