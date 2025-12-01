package com.example.machteacher.repository

import com.example.machteacher.model.Session
import com.example.machteacher.dto.SessionRequest
import kotlinx.coroutines.flow.Flow

interface SessionRepository {

    fun getUpcomingSessions(): Flow<List<Session>>

    suspend fun refreshUpcomingSessions()

    suspend fun insertSession(
        mentorId: Long,
        subject: String,
        mentorName: String,
        dateTime: String,
        modality: String
    )

    /**
     * Crea la sesión en el backend y devuelve el ID recién creado.
     */
    suspend fun createSession(
        request: SessionRequest
    ): Long

    /**
     * Crea automáticamente la conversación asociada a la sesión recién creada.
     */
    suspend fun startConversation(sessionId: Long): Long

    suspend fun deleteSession(id: Long)
}
