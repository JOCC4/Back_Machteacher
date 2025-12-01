package com.example.machteacher.repository

import com.example.machteacher.api.SosApi
import com.example.machteacher.dto.SosAcceptRequestDto
import com.example.machteacher.dto.SosCreateRequestDto
import com.example.machteacher.dto.SosResponseDto
import javax.inject.Inject

class SosRepositoryImpl @Inject constructor(
    private val api: SosApi
) : SosRepository {

    override suspend fun sendSos(
        studentId: Long,
        subject: String?,
        message: String?
    ): SosResponseDto {
        val body = SosCreateRequestDto(
            studentId = studentId,
            subject = subject,
            message = message
        )
        return api.createSos(body)
    }

    override suspend fun getActiveSos(): List<SosResponseDto> =
        api.getActiveSos()

    override suspend fun acceptSos(
        sosId: Long,
        mentorId: Long
    ): SosResponseDto {
        val body = SosAcceptRequestDto(mentorId = mentorId)
        return api.acceptSos(sosId, body)
    }

    override suspend fun getSosByStudent(studentId: Long): List<SosResponseDto> =
        api.getSosByStudent(studentId)

    override suspend fun getSosByMentor(mentorId: Long): List<SosResponseDto> =
        api.getSosByMentor(mentorId)
}
