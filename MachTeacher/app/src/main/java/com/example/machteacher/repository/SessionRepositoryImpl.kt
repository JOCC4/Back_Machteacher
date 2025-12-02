package com.example.machteacher.repository

import android.util.Log
import com.example.machteacher.api.CatalogApi
import com.example.machteacher.api.SessionApi
import com.example.machteacher.api.MessagingApi
import com.example.machteacher.dao.SessionDao
import com.example.machteacher.dto.SessionRequest
import com.example.machteacher.entity.SessionEntity
import com.example.machteacher.model.Session
import com.example.machteacher.storage.AppDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepositoryImpl @Inject constructor(
    private val dao: SessionDao,
    private val api: SessionApi,
    private val messagingApi: MessagingApi,
    private val dataStore: AppDataStore,
    private val catalogApi: CatalogApi,
    private val profileRepository: ProfileRepository
) : SessionRepository {

    override fun getUpcomingSessions(): Flow<List<Session>> {
        return dataStore.getUserIdAsFlow().flatMapLatest { userId ->
            if (userId == null) return@flatMapLatest flowOf(emptyList())

            val role = (dataStore.getUserRole() ?: "STUDENT")
                .uppercase()
                .removePrefix("ROLE_")

            val baseFlow = when (role) {
                "MENTOR" -> dao.getAllSessionsAsMentor(userId)
                else -> dao.getAllSessions(userId)
            }

            baseFlow.map { list ->
                list.map { e ->
                    Session(
                        id = e.id,
                        mentorId = e.mentorId,
                        subject = e.subject,
                        mentorName = e.mentorName,
                        dateTime = e.dateTime,
                        modality = e.modality
                    )
                }
            }
        }
    }

    override suspend fun refreshUpcomingSessions() {
        try {
            val userId = dataStore.getUserId() ?: return
            val role = (dataStore.getUserRole() ?: "STUDENT")
                .uppercase()
                .removePrefix("ROLE_")

            val subjects = runCatching { catalogApi.listSubjects() }.getOrNull().orEmpty()
            val subjectNameById = subjects.associateBy({ it.id }, { it.name })

            val dtos = when (role) {
                "MENTOR" -> api.listByMentor(userId)
                else -> api.listByStudent(userId)
            }

            val entities = dtos.mapNotNull { dto ->
                val subjectName = subjectNameById[dto.subjectId] ?: return@mapNotNull null

                val mentorProfile = runCatching {
                    profileRepository.getMentorProfile(dto.mentorId)
                }.getOrNull()
                val mentorName = mentorProfile?.name ?: "Mentor ${dto.mentorId}"

                SessionEntity(
                    id = dto.id,
                    studentId = dto.studentId,
                    mentorId = dto.mentorId,
                    subject = subjectName,
                    mentorName = mentorName,
                    dateTime = "${dto.date} ${dto.startTime}",
                    modality = dto.modality
                )
            }

            dao.clearAll()
            dao.insertAll(entities)

        } catch (e: Exception) {
            Log.e("SessionRepository", "Failed to refresh sessions", e)
            throw e
        }
    }

    override suspend fun insertSession(
        mentorId: Long,
        subject: String,
        mentorName: String,
        dateTime: String,
        modality: String
    ) {
        val userId = dataStore.getUserId() ?: return

        val entity = SessionEntity(
            studentId = userId,
            mentorId = mentorId,
            subject = subject,
            mentorName = mentorName,
            dateTime = dateTime,
            modality = modality
        )

        dao.insertSession(entity)
    }

    override suspend fun createSession(request: SessionRequest): Long {
        return api.createSession(request).id
    }

    override suspend fun startConversation(sessionId: Long): Long {
        return messagingApi.startConversationBySession(sessionId)
    }

    override suspend fun deleteSession(id: Long) {
        try {
            val resp = api.deleteSession(id)
            if (resp.isSuccessful) {
                dao.deleteById(id)
            } else {
                throw Exception("Error al borrar sesión. HTTP ${resp.code()}")
            }
        } catch (e: Exception) {
            Log.e("SessionRepository", "Failed to delete session $id", e)
            throw e
        }
    }
}
