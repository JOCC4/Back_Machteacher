package com.example.machteacher.api

import com.example.machteacher.dto.ConversationPreviewDto
import com.example.machteacher.dto.MessageDto
import com.example.machteacher.dto.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MessagingApi {

    @POST("api/messages/conversations/by-session/{sessionId}")
    suspend fun startConversationBySession(
        @Path("sessionId") sessionId: Long
    ): Long

    @GET("api/messages/conversations/{conversationId}")
    suspend fun listMessages(
        @Path("conversationId") conversationId: Long,
        @Query("userId") userId: Long
    ): List<MessageDto>

    @POST("api/messages/conversations/{conversationId}/send")
    suspend fun sendMessage(
        @Path("conversationId") conversationId: Long,
        @Body request: SendMessageRequest
    ): MessageDto

    @GET("api/messages/user/{userId}")
    suspend fun listUserConversations(
        @Path("userId") userId: Long
    ): List<ConversationPreviewDto>

    @POST("api/messages/conversations/{conversationId}/read")
    suspend fun markConversationAsRead(
        @Path("conversationId") conversationId: Long,
        @Query("userId") userId: Long
    ): Unit
}
