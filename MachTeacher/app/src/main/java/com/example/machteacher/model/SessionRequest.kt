package com.example.machteacher.model

data class SessionRequest(
    val mentorId: Long,
    val studentId: Long?,
    val subjectId: Long,
    val packageTypeId: Long?,
    val date: String,
    val startTime: String,
    val durationMinutes: Int,
    val modality: String,
    val notes: String?,
    val priceUsd: Double
)
