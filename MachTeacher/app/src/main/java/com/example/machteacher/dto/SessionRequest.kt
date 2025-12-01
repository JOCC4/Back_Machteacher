package com.example.machteacher.dto

data class SessionRequest(
    val mentorId: Long,
    val studentId: Long?,        // siempre será null, backend lo reemplaza
    val subjectId: Long,
    val packageTypeId: Long?,
    val date: String,            // "yyyy-MM-dd"
    val startTime: String,       // "HH:mm"
    val durationMinutes: Int,
    val modality: String,        // "ONLINE" o "PRESENCIAL"
    val notes: String?,
    val priceUsd: Double
)

