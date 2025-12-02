package com.example.machteacher.repository

import com.example.machteacher.api.CatalogApi
import com.example.machteacher.api.ProfileApi
import com.example.machteacher.dto.ProfileDto
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi,
    private val catalogApi: CatalogApi
) : ProfileRepository {

    // ---------- STUDENT ----------
    override suspend fun getStudentProfile(userId: Long): ProfileDto =
        try {
            api.getStudentProfile(userId)
        } catch (e: IOException) {
            throw IOException("Sin conexión al servidor")
        } catch (e: HttpException) {
            throw e
        }

    override suspend fun updateStudentProfile(userId: Long, body: ProfileDto): ProfileDto =
        try {
            api.updateStudentProfile(userId, body)
        } catch (e: IOException) {
            throw IOException("Sin conexión al servidor")
        } catch (e: HttpException) {
            throw e
        }

    // ---------- MENTOR ----------
    override suspend fun getMentorProfile(userId: Long): ProfileDto =
        try {
            api.getMentorProfile(userId)
        } catch (e: IOException) {
            throw IOException("Sin conexión al servidor")
        } catch (e: HttpException) {
            throw e
        }

    override suspend fun updateMentorProfile(userId: Long, body: ProfileDto): ProfileDto =
        try {
            api.updateMentorProfile(userId, body)
        } catch (e: IOException) {
            throw IOException("Sin conexión al servidor")
        } catch (e: HttpException) {
            throw e
        }

    // ---------- LISTA DE MENTORES ----------
    override suspend fun getMentors(page: Int, size: Int, query: String?): List<ProfileDto> =
        try {
            val resp = api.getMentors(
                page = page,
                size = size,
                q = query?.takeIf { it.isNotBlank() }
            )
            resp.content
        } catch (e: IOException) {
            throw IOException("Sin conexión al servidor")
        } catch (e: HttpException) {
            throw e
        }

    // ---------- CATÁLOGO ----------
    override suspend fun getSubjectIdByName(name: String): Long {
        val all = catalogApi.listSubjects()
        return all.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?.id ?: throw IllegalArgumentException("Materia '$name' no encontrada en el catálogo")
    }
}
