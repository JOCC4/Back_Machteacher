// app/src/main/java/com/example/machteacher/repository/MessagingRepositoryImpl.kt
package com.example.machteacher.repository

import com.example.machteacher.api.MessagingApi
import com.example.machteacher.dto.SendMessageRequest
import com.example.machteacher.model.ChatMessage
import com.example.machteacher.model.ConversationPreview
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagingRepositoryImpl @Inject constructor(
    private val api: MessagingApi
) : MessagingRepository {

    override suspend fun ensureConversationForSession(sessionId: Long): Long {
        return api.startConversationBySession(sessionId)
    }

    override suspend fun getMessages(
        conversationId: Long,
        userId: Long
    ): List<ChatMessage> {
        val dtos = api.listMessages(conversationId, userId)
        return dtos.map { dto ->
            ChatMessage(
                id = dto.id,
                text = dto.content,
                mine = dto.mine,
                createdAt = dto.sentAt,
                read = dto.read
            )
        }
    }

    override suspend fun sendMessage(
        conversationId: Long,
        senderId: Long,
        body: String
    ): ChatMessage {
        val dto = api.sendMessage(
            conversationId,
            SendMessageRequest(senderId = senderId, body = body)
        )
        return ChatMessage(
            id = dto.id,
            text = dto.content,
            mine = dto.mine,
            createdAt = dto.sentAt,
            read = dto.read
        )
    }

    override suspend fun loadUserConversations(userId: Long): List<ConversationPreview> {
        val dtos = api.listUserConversations(userId)
        return dtos.map { dto ->
            ConversationPreview(
                id = dto.id,
                mentorName = dto.mentorName,
                lastMessage = dto.lastMessage,
                timeLabel = dto.timeLabel,
                unread = dto.unread
            )
        }
    }

    override suspend fun markConversationAsRead(
        conversationId: Long,
        userId: Long
    ) {
        api.markConversationAsRead(conversationId, userId)
    }
}
