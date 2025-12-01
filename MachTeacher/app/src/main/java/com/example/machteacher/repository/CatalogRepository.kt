package com.example.machteacher.repository

import com.example.machteacher.api.CatalogApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepository @Inject constructor(
    private val api: CatalogApi
) {
    suspend fun getSubjectIdByName(name: String): Long {
        // Si tienes búsqueda server-side, descomenta lo de abajo y borra el bloque de listSubjects()
        // val matches = api.searchSubjects(name)
        // return matches.firstOrNull { it.name.equals(name, ignoreCase = true) }
        //     ?.id ?: throw IllegalArgumentException("Materia no encontrada: $name")

        // Listado completo y filtro en cliente:
        val all = api.listSubjects()
        return all.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?.id ?: throw IllegalArgumentException("Materia no encontrada: $name")
    }
}