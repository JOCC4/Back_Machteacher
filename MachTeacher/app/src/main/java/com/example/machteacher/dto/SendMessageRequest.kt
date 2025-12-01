// app/src/main/java/com/example/machteacher/dto/SendMessageRequest.kt
package com.example.machteacher.dto

data class SendMessageRequest(
    val senderId: Long,
    val body: String
)
