package com.example.machteacher.dto

data class SosUserDto(
    val id: Long,
    val fullName: String?
)


data class SosResponseDto(
    val id: Long,
    val student: SosUserDto?,
    val acceptedBy: SosUserDto?,
    val subject: String?,
    val message: String?,
    val status: String,
    val createdAt: String?,
    val acceptedAt: String?,
    val sessionId: Long?,
    val conversationId: Long?
)
