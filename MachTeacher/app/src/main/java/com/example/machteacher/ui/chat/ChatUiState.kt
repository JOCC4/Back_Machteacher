// app/src/main/java/com/example/machteacher/ui/chat/ChatUiState.kt
package com.example.machteacher.ui.chat

import com.example.machteacher.model.ChatMessage

data class ChatUiState(
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val input: String = "",
    val error: String? = null,
    val conversationId: Long? = null,
    val messages: List<ChatMessage> = emptyList()
)
