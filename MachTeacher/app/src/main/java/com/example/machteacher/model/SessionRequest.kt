// app/src/main/java/com/example/machteacher/model/SessionRequest.kt
package com.example.machteacher.model

data class SessionRequest(
    val mentorId: Long,
    val studentId: Long?,        // el backend lo ignora y toma del token; puedes enviar null
    val subjectId: Long,
    val packageTypeId: Long?,    // null si sesión individual
    val date: String,            // "yyyy-MM-dd"
    val startTime: String,       // "HH:mm"
    val durationMinutes: Int,    // 60, 90, 120...
    val modality: String,        // "ONLINE" | "PRESENCIAL"
    val notes: String?,          // null si vacío
    val priceUsd: Double
)
