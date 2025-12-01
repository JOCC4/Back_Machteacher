// com/example/machteacher/dto/SessionDto.kt
package com.example.machteacher.dto

data class SessionDto(
    val id: Long,
    val studentId: Long,
    val mentorId: Long,
    val subjectId: Long,
    val packageTypeId: Long?,   // puede venir null
    val date: String,           // "2025-11-18"
    val startTime: String,      // "12:00"
    val durationMinutes: Int,
    val modality: String,       // "ONLINE" / "PRESENCIAL"
    val status: String,         // "SCHEDULED"
    val priceUsd: Double,
    val notes: String?          // puede ser null
)
