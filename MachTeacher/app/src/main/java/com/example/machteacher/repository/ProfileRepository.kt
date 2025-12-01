package com.example.machteacher.repository

import com.example.machteacher.dto.ProfileDto

interface ProfileRepository {

    // ---------- STUDENT ----------
    suspend fun getStudentProfile(userId: Long): ProfileDto
    suspend fun updateStudentProfile(userId: Long, body: ProfileDto): ProfileDto

    // ---------- MENTOR ----------
    suspend fun getMentorProfile(userId: Long): ProfileDto
    suspend fun updateMentorProfile(userId: Long, body: ProfileDto): ProfileDto

    // ---------- LISTA DE MENTORES ----------
    suspend fun getMentors(page: Int, size: Int, query: String? = null): List<ProfileDto>

    // ---------- CATÁLOGO ----------
    suspend fun getSubjectIdByName(name: String): Long
}
