package com.example.machteacher.api


import com.example.machteacher.dto.MessageDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ChatApi {
    @GET("/api/chat/{conversationId}")
    suspend fun getMessages(@Path("conversationId") id: Long): List<MessageDto>
}