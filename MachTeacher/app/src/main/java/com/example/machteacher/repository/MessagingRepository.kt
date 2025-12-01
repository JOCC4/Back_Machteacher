// app/src/main/java/com/example/machteacher/repository/MessagingRepository.kt
package com.example.machteacher.repository

import com.example.machteacher.model.ChatMessage
import com.example.machteacher.model.ConversationPreview

interface MessagingRepository {

    suspend fun ensureConversationForSession(sessionId: Long): Long

    suspend fun getMessages(
        conversationId: Long,
        userId: Long
    ): List<ChatMessage>

    suspend fun sendMessage(
        conversationId: Long,
        senderId: Long,
        body: String
    ): ChatMessage

    // ✅ Nuevo: usar /api/messages/user/{userId} del backend
    suspend fun loadUserConversations(
        userId: Long
    ): List<ConversationPreview>

    // ✅ Nuevo: marcar como leída una conversación para un usuario
    // POST /api/messages/conversations/{conversationId}/read?userId={userId}
    suspend fun markConversationAsRead(
        conversationId: Long,
        userId: Long
    )
}
