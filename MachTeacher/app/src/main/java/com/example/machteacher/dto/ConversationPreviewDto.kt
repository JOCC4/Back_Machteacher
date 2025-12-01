// app/src/main/java/com/example/machteacher/dto/ConversationPreviewDto.kt
package com.example.machteacher.dto

data class ConversationPreviewDto(
    val id: Long,
    val mentorName: String,
    val lastMessage: String,
    val timeLabel: String,
    val unread: Int
)