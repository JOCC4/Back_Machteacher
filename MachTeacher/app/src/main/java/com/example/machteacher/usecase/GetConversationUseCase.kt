package com.example.machteacher.usecase

import com.example.machteacher.repository.ChatRepository
import com.example.machteacher.model.Message
import javax.inject.Inject

class GetConversationUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(id: Long): Result<List<Message>> {
        return try {
            val messages = repository.getConversationById(id)
            Result.success(messages)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}