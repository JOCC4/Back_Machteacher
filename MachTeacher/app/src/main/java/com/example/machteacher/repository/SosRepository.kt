package com.example.machteacher.repository

import com.example.machteacher.dto.SosResponseDto

interface SosRepository {

    suspend fun sendSos(
        studentId: Long,
        subject: String?,
        message: String?
    ): SosResponseDto

    suspend fun getActiveSos(): List<SosResponseDto>

    suspend fun acceptSos(
        sosId: Long,
        mentorId: Long
    ): SosResponseDto

    suspend fun getSosByStudent(studentId: Long): List<SosResponseDto>

    suspend fun getSosByMentor(mentorId: Long): List<SosResponseDto>
}
