package com.example.machteacher.model

data class ChatMessage(
    val id: Long = 0L,
    val text: String = "",
    val mine: Boolean = false,
    val createdAt: String = "",
    val read: Boolean = false
)


