package com.example.machteacher.model




data class Message(
    val id: Int,
    val sender: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

