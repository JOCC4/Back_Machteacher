package com.example.machteacher.dto


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
    val subjects: String? = null,
    val hourlyRate: String? = null,
    val teachingExperience: String? = null,
    val aboutMe: String? = null,
    val references: String? = null
)
