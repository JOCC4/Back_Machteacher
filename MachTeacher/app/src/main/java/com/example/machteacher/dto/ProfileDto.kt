package com.example.machteacher.dto

// El backend envía todo como Strings, el frontend se encarga de convertir
data class ProfileDto(
    val id: Long?,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val location: String? = null,
    val role: String? = null,

    val bio: String? = null,
    val university: String? = null,
    val career: String? = null,
    val semester: String? = null,

    // Mentor extras
    val subjects: String? = null, // CSV: "Programación,Algoritmos,...
    val hourlyRate: String? = null, // Se recibe como String
    val teachingExperience: String? = null,
    val aboutMe: String? = null,
    val references: String? = null
)
