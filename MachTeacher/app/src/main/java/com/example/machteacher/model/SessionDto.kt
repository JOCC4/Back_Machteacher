package com.example.machteacher.model

data class SessionDto(
    val id: Long,
    val studentId: Long,
    val mentorId: Long,
    val subjectId: Long,
    val packageTypeId: Long?,
    val date: String,
    val startTime: String,
    val durationMinutes: Int,
    val modality: String,
    val status: String,
    val priceUsd: Double,
    val notes: String?
)
