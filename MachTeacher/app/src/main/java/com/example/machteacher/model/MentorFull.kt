package com.example.machteacher.model

import androidx.compose.runtime.Immutable

@Immutable
data class MentorFull(
    val id: Long,
    val name: String,
    val degree: String,
    val subjects: List<String>,
    val about: String,
    val pricePerHour: String
)
