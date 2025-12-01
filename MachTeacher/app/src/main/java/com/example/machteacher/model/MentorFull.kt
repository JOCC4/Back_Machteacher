package com.example.machteacher.model

import androidx.compose.runtime.Immutable

// Modelo de datos para mostrar los detalles de un mentor en la pantalla de reserva.
// Contiene solo los datos que podemos obtener del ProfileRepository.
@Immutable
data class MentorFull(
    val id: Long,
    val name: String,
    val degree: String,
    val subjects: List<String>,
    val about: String,
    val pricePerHour: String
)
