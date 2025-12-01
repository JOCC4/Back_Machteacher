package com.example.machteacher.dto

/** Lo que envía el alumno al crear un SOS */
data class SosCreateRequestDto(
    val studentId: Long,
    val subject: String?,
    val message: String?
)

/** Lo que envía el mentor al aceptar un SOS */
data class SosAcceptRequestDto(
    val mentorId: Long
)
