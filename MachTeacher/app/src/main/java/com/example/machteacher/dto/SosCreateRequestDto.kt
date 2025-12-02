package com.example.machteacher.dto


data class SosCreateRequestDto(
    val studentId: Long,
    val subject: String?,
    val message: String?
)


data class SosAcceptRequestDto(
    val mentorId: Long
)
