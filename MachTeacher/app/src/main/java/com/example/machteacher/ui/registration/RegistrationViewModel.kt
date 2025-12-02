package com.example.machteacher.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.dto.AuthResponse
import com.example.machteacher.dto.ProfileDto
import com.example.machteacher.dto.RegisterRequest
import com.example.machteacher.repository.AuthRepository
import com.example.machteacher.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

data class RegistrationState(
    val loading: Boolean = false,
    val error: String? = null,
    val done: Boolean = false,
    val result: AuthResponse? = null
)

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val profileRepo: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegistrationState())
    val state: StateFlow<RegistrationState> = _state


    fun registerStudent(
        email: String, fullName: String, phone: String, city: String, country: String,
        password: String, university: String?, career: String?, semester: String?
    ) {
        if (_state.value.loading) return
        _state.value = RegistrationState(loading = true)

        viewModelScope.launch {
            try {
                val req = RegisterRequest(
                    email = email, fullName = fullName, role = "STUDENT",
                    phone = phone, city = city, country = country, password = password,
                    university = university, career = career, semester = semester
                )
                val resp = authRepo.register(req)
                _state.value = RegistrationState(done = true, result = resp)

            } catch (e: Exception) {
                _state.value = RegistrationState(error = humanize(e))
            }
        }
    }

    // --- REGISTRO DE MENTOR ---
    fun registerMentor(
        email: String, fullName: String, phone: String, city: String, country: String,
        password: String, university: String?, career: String?, semester: String?,
        subjects: String?, hourlyRate: String?, teachingExperience: String?,
        aboutMe: String?, references: String?
    ) {
        if (_state.value.loading) return
        _state.value = RegistrationState(loading = true)

        viewModelScope.launch {
            try {
                // 1. Crear el usuario básico
                val registerReq = RegisterRequest(
                    email = email, fullName = fullName, role = "MENTOR", phone = phone, city = city,
                    country = country, password = password, university = university,
                    career = career, semester = semester
                )
                val authResponse = authRepo.register(registerReq)


                val newUserId = authResponse.id


                val profileUpdateReq = ProfileDto(
                    id = newUserId,
                    subjects = subjects,
                    hourlyRate = hourlyRate,
                    teachingExperience = teachingExperience,
                    aboutMe = aboutMe,
                    references = references
                )
                profileRepo.updateMentorProfile(newUserId, profileUpdateReq)


                _state.value = RegistrationState(done = true, result = authResponse)

            } catch (e: Exception) {
                _state.value = RegistrationState(error = humanize(e))
            }
        }
    }

    private fun humanize(e: Exception): String =
        when (e) {
            is HttpException -> "Error HTTP ${e.code()}: ${e.message()}"
            is IOException -> "Sin conexión. Revisa tu conexión a internet."
            else -> e.message ?: "Ocurrió un error inesperado"
        }
}
