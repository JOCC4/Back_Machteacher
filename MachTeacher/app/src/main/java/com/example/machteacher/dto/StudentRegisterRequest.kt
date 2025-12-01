package com.example.machteacher.dto


data class StudentRegisterRequest(
    val roleUi: String,
    val email: String,
    val fullName: String,
    val university: String,
    val career: String,
    val semester: String,
    val phone: String,
    val location: String
)
