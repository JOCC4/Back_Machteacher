package com.example.machteacher.model

data class ConversationPreview(
    val id: Long,
    val mentorName: String,
    val lastMessage: String,
    val timeLabel: String,
    val unread: Int
)