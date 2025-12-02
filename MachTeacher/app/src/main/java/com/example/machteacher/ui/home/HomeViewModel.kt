package com.example.machteacher.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.api.CatalogApi
import com.example.machteacher.api.ProfileApi
import com.example.machteacher.api.SessionApi
import com.example.machteacher.dto.ProfileDto
import com.example.machteacher.dto.SessionDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val loading: Boolean = false,
    val userName: String = "",
    val sessions: List<HomeSessionUi> = emptyList(),
    val featured: List<HomeMentorUi> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileApi: ProfileApi,
    private val sessionApi: SessionApi,
    private val catalogApi: CatalogApi
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state

    fun load(
        currentUserId: Long,
        role: String,
        featuredIds: List<Long> = listOf(19L, 25L)
    ) {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)

        val normRole = role.uppercase().removePrefix("ROLE_")

        viewModelScope.launch {
            try {
                // 1) Perfil para el saludo
                val me: ProfileDto? = run {
                    val mentorResp = try {
                        profileApi.getProfile("mentor", currentUserId)
                    } catch (_: Exception) { null }

                    val mentorBody = mentorResp?.takeIf { it.isSuccessful }?.body()

                    if (mentorBody != null) mentorBody
                    else {
                        val studentResp = try {
                            profileApi.getProfile("student", currentUserId)
                        } catch (_: Exception) { null }
                        studentResp?.takeIf { it.isSuccessful }?.body()
                    }
                }

                val userName = me?.name?.takeIf { it.isNotBlank() } ?: me?.email ?: "Usuario"

                // 2) Mapa de materias id -> nombre
                val subjectMap = try {
                    catalogApi.listSubjects().associateBy { it.id }
                } catch (_: Exception) {
                    emptyMap<Long, com.example.machteacher.dto.SubjectDto>()
                }

                // 3) Sesiones según rol
                val sessionDtos: List<SessionDto> = try {
                    when (normRole) {
                        "MENTOR" -> sessionApi.listByMentor(currentUserId)
                        else -> sessionApi.listByStudent(currentUserId)
                    }
                } catch (_: Exception) {
                    emptyList()
                }

                val allSessions: List<HomeSessionUi> = sessionDtos.map { dto ->
                    val subjectName = subjectMap[dto.subjectId]?.name
                        ?: "Materia ${dto.subjectId}"

                    val otherName: String = try {
                        if (normRole == "MENTOR") {

                            val r = profileApi.getProfile("student", dto.studentId)
                            if (r.isSuccessful) {
                                r.body()?.name ?: "Alumno ${dto.studentId}"
                            } else {
                                "Alumno ${dto.studentId}"
                            }
                        } else {

                            val r = profileApi.getProfile("mentor", dto.mentorId)
                            if (r.isSuccessful) {
                                r.body()?.name ?: "Mentor ${dto.mentorId}"
                            } else {
                                "Mentor ${dto.mentorId}"
                            }
                        }
                    } catch (_: Exception) {
                        if (normRole == "MENTOR") "Alumno ${dto.studentId}" else "Mentor ${dto.mentorId}"
                    }

                    HomeSessionUi(
                        id = dto.id.toString(),
                        subject = subjectName,
                        mentorName = otherName,
                        whenText = "${dto.date} ${dto.startTime}",
                        mode = dto.modality
                    )
                }


                val sessionsLimited = allSessions.take(2)


                val featured = featuredIds.mapNotNull { id ->
                    try {
                        val r = profileApi.getProfile("mentor", id)
                        val p = r.body() ?: return@mapNotNull null
                        p.toHomeMentorUi()
                    } catch (_: Exception) {
                        null
                    }
                }

                _state.value = HomeState(
                    loading = false,
                    userName = userName,
                    sessions = sessionsLimited,
                    featured = featured,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Error cargando inicio"
                )
            }
        }
    }

    private fun ProfileDto.toHomeMentorUi(): HomeMentorUi {
        val nameSafe = (name ?: email ?: "Mentor").trim()

        val tags = subjects
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()
            .let { list ->
                if (list.size <= 2) list
                else list.take(2) + "+${list.size - 2}"
            }

        val initials = nameSafe.firstOrNull()?.uppercase() ?: "M"

        return HomeMentorUi(
            id = this.id ?: 0L,
            name = nameSafe,
            area = career ?: university ?: "—",
            tags = if (tags.isEmpty()) listOf("—") else tags,
            price = hourlyRate?.let { "$it/hr" } ?: "—",
            nextWhen = "—",
            rating = "—",
            ratingCount = 0,
            online = true,
            initials = initials
        )
    }
}
