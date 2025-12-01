// app/src/main/java/com/example/machteacher/model/SessionDto.kt
package com.example.machteacher.model

data class SessionDto(
    val id: Long,
    val studentId: Long,
    val mentorId: Long,
    val subjectId: Long,
    val packageTypeId: Long?,
    val date: String,            // "yyyy-MM-dd"
    val startTime: String,       // "HH:mm"
    val durationMinutes: Int,
    val modality: String,        // "ONLINE" | "PRESENCIAL"
    val status: String,          // "SCHEDULED", "COMPLETED", etc.
    val priceUsd: Double,
    val notes: String?
)
