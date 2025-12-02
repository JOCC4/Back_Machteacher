package com.example.machteacher.ui.search

import androidx.compose.runtime.Immutable
import com.example.machteacher.dto.ProfileDto
import kotlin.random.Random

@Immutable
data class MentorUi(
    val id: Long,
    val name: String,
    val degree: String,
    val subjects: List<String>,
    val badges: List<String>,
    val pricePerHour: String,
    val nextSlot: String,
    val rating: String,
    val reviews: Int,
    val online: Boolean,
    val initials: String
)


fun ProfileDto.toMentorUi(): MentorUi? {
    if (id == null) {
        return null
    }

    val mentorSubjects = subjects.orEmpty()
        .split(',')
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    val mentorInitials = name.orEmpty().split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull() }
        .joinToString("")
        .uppercase()


    return MentorUi(
        id = id,
        name = name.orEmpty(),
        degree = career.orEmpty(),
        subjects = mentorSubjects,
        badges = if (Random.nextBoolean()) listOf("Top Mentor") else emptyList(),
        pricePerHour = hourlyRate?.let { "$$it / hr" } ?: "No disponible",
        nextSlot = "Disponible ahora",
        rating = String.format("%.1f", 4.5 + Random.nextFloat() * 0.5),
        reviews = Random.nextInt(5, 50),
        online = Random.nextBoolean(),
        initials = mentorInitials
    )
}
