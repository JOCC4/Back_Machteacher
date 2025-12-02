package com.example.machteacher.dto

data class MessageDto(
    val id: Long,
    val conversationId: Long,
    val senderId: Long,
    val receiverId: Long?,
    val sessionId: Long?,
    val content: String,
    val sentAt: String,
    val mine: Boolean,
    val read: Boolean
)
