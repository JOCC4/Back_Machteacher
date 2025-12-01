package com.example.machteacher.usecase


import com.example.machteacher.model.Session
import com.example.machteacher.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUpcomingSessionsUseCase @Inject constructor(
    private val repo: SessionRepository
) {
    operator fun invoke(): Flow<List<Session>> = repo.getUpcomingSessions()
}
