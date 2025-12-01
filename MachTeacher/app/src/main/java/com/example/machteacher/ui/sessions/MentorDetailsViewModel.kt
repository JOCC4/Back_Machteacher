package com.example.machteacher.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.dto.ProfileDto
import com.example.machteacher.model.ProfileUi
import com.example.machteacher.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class MentorDetailsState(
    val loading: Boolean = false,
    val error: String? = null,
    val done: Boolean = false,
    val profile: ProfileUi? = null
)

@HiltViewModel
class MentorDetailsViewModel @Inject constructor(
    private val profileRepo: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MentorDetailsState())
    val state: StateFlow<MentorDetailsState> = _state

    fun loadMentor(userId: Long) {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)

        viewModelScope.launch {
            try {
                val dto = profileRepo.getMentorProfile(userId)
                _state.value = _state.value.copy(
                    loading = false,
                    profile = toUi(dto)
                )
            } catch (e: IOException) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "Sin conexión al servidor"
                )
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "HTTP ${e.code()}"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Error cargando mentor"
                )
            }
        }
    }

    fun saveMentor(
        userId: Long,
        subjects: List<String>,
        hourlyRate: String?,
        teachingExp: String?,
        aboutMe: String?,
        references: String?,
        university: String?,
        career: String?,
        semester: String?
    ) {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null, done = false)

        // REVERTIDO: Volvemos a crear un String separado por comas
        val subjectsCsv = subjects.joinToString(",")

        // REVERTIDO: El constructor vuelve a ser simple
        val updateBody = ProfileDto(
            id = userId,
            name = null,
            email = null,
            phone = null,
            location = null,
            role = null,
            bio = null,
            university = university,
            career = career,
            semester = semester,
            subjects = subjectsCsv,
            hourlyRate = hourlyRate,
            teachingExperience = teachingExp,
            aboutMe = aboutMe,
            references = references
        )

        viewModelScope.launch {
            try {
                val updated = profileRepo.updateMentorProfile(userId, updateBody)
                _state.value = _state.value.copy(
                    loading = false,
                    done = true,
                    profile = toUi(updated)
                )
            } catch (e: IOException) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "Sin conexión al servidor"
                )
            } catch (e: HttpException) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = "HTTP ${e.code()}"
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    loading = false,
                    error = e.message ?: "Error"
                )
            }
        }
    }

    // ---------- Mapper DTO -> UI ----------
    private fun toUi(dto: ProfileDto): ProfileUi =
        ProfileUi(
            name = dto.name,
            bio = dto.bio,
            email = dto.email,
            phone = dto.phone,
            university = dto.university,
            career = dto.career,
            semester = dto.semester,
            location = dto.location,
            role = dto.role,
            // REVERTIDO: Volvemos a usar split
            subjects = dto.subjects?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() },
            // REVERTIDO: hourlyRate ya es String
            hourlyRate = dto.hourlyRate,
            teachingExperience = dto.teachingExperience,
            aboutMe = dto.aboutMe,
            references = dto.references
        )
}
