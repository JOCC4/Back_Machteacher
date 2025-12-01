package com.example.machteacher.dto

/** Mini-DTO para el usuario que llega dentro del SOS */
data class SosUserDto(
    val id: Long,
    val fullName: String?
)

/** Respuesta principal del backend para un SOS */
data class SosResponseDto(
    val id: Long,
    val student: SosUserDto?,        // Alumno que creó el SOS
    val acceptedBy: SosUserDto?,     // Mentor que lo aceptó (si aplica)
    val subject: String?,
    val message: String?,
    val status: String,              // "PENDING", "ACCEPTED", etc.
    val createdAt: String?,          // ISO string
    val acceptedAt: String?,         // ISO string
    val sessionId: Long?,            // Puede ser null si aún no hay sesión
    val conversationId: Long?        // Puede ser null si aún no hay conversación
)
