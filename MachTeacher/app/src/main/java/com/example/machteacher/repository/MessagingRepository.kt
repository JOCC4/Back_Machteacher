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


    suspend fun loadUserConversations(
        userId: Long
    ): List<ConversationPreview>


    suspend fun markConversationAsRead(
        conversationId: Long,
        userId: Long
    )
}
