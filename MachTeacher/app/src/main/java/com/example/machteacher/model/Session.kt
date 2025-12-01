package com.example.machteacher.model

data class Session(
    val id: Long,
    val mentorId: Long,
    val subject: String,
    val mentorName: String,
    val dateTime: String,
    val modality: String
)