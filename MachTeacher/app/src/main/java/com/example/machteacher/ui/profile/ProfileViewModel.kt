// app/src/main/java/com/example/machteacher/ui/profile/ProfileViewModel.kt
package com.example.machteacher.ui.profile

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

data class ProfileState(
    val loading: Boolean = false,
    val data: ProfileUi? = null,
    val error: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repo: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state

    fun load(role: String, userId: Long) {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)

        viewModelScope.launch {
            try {
                val dto = if (role.equals("MENTOR", true)) {
                    repo.getMentorProfile(userId)
                } else {
                    repo.getStudentProfile(userId)
                }
                _state.value = ProfileState(
                    data = dto.toUi(),
                    loading = false
                )
            } catch (e: IOException) {
                _state.value = ProfileState(loading = false, error = "Sin conexión al servidor")
            } catch (e: HttpException) {
                _state.value = ProfileState(loading = false, error = "Error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                _state.value = ProfileState(loading = false, error = e.message ?: "Error inesperado")
            }
        }
    }

    /** Guarda SOLO about_me. */
    fun updateBio(role: String, userId: Long, newBio: String) {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)

        val clean = newBio.trim()

        // REVERTIDO: El constructor vuelve a ser simple
        val body = if (role.equals("MENTOR", true)) {
            ProfileDto(
                id = userId,
                aboutMe = clean
            )
        } else {
            ProfileDto(
                id = userId,
                bio = clean
            )
        }

        viewModelScope.launch {
            try {
                val updated = if (role.equals("MENTOR", true)) {
                    repo.updateMentorProfile(userId, body)
                } else {
                    repo.updateStudentProfile(userId, body)
                }
                _state.value = _state.value.copy(
                    loading = false,
                    data = updated.toUi()
                )
            } catch (e: IOException) {
                _state.value = _state.value.copy(loading = false, error = "Sin conexión al servidor")
            } catch (e: HttpException) {
                _state.value = _state.value.copy(loading = false, error = "Error ${e.code()}: ${e.message()}")
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message ?: "Error guardando bio")
            }
        }
    }


    // Mapper DTO -> UI (mostramos aboutMe con prioridad)
    private fun ProfileDto.toUi(): ProfileUi =
        ProfileUi(
            name = name,
            bio = aboutMe ?: bio,
            email = email,
            phone = phone,
            university = university,
            career = career,
            semester = semester,
            location = location,
            role = role,
            // REVERTIDO: Volvemos a usar split para procesar el String
            subjects = subjects?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() },
            // REVERTIDO: hourlyRate ya es un String y se pasa directamente
            hourlyRate = hourlyRate,
            teachingExperience = teachingExperience,
            aboutMe = aboutMe,
            references = references
        )
}
