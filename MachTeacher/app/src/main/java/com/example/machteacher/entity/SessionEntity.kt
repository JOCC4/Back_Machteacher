package com.example.machteacher.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey
    val id: Long = 0,
    val studentId: Long,
    val mentorId: Long,
    val subject: String,
    val mentorName: String,
    val dateTime: String,
    val modality: String
)
