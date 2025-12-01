package com.example.machteacher.search

import com.example.machteacher.dto.ProfileDto

data class MentorUi(
    val id: Long?,
    val name: String,
    val career: String,
    val subjects: String,
    val hourlyRate: String
)

/** Convierte un [ProfileDto] de la capa de datos a un [MentorUi] para la UI. */
fun ProfileDto.toMentorUi(): MentorUi {
    return MentorUi(
        id = this.id,
        name = this.name.orEmpty(),
        career = this.career.orEmpty(),
        subjects = this.subjects.orEmpty(),
        hourlyRate = this.hourlyRate.orEmpty()
    )
}
