package com.example.machteacher.model

data class ProfileUi(
    // comunes
    val name: String? = null,
    val bio: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val university: String? = null,
    val career: String? = null,
    val semester: String? = null,
    val location: String? = null,
    val role: String? = null,


    val subjects: List<String>? = null,
    val hourlyRate: String? = null,
    val teachingExperience: String? = null,
    val aboutMe: String? = null,
    val references: String? = null
)
