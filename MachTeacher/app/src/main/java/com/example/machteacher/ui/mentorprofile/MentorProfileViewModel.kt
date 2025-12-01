package com.example.machteacher.ui.mentorprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.api.ProfileApi
import com.example.machteacher.dto.ProfileDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MentorProfileState(
    val loading: Boolean = false,
    val error: String? = null,
    val profile: ProfileDto? = null
)

@HiltViewModel
class MentorProfileViewModel @Inject constructor(
    private val profileApi: ProfileApi
) : ViewModel() {

    private val _state = MutableStateFlow(MentorProfileState())
    val state: StateFlow<MentorProfileState> = _state

    fun loadMentorProfile(userId: Long) {
        if (_state.value.loading) return
        _state.value = _state.value.copy(loading = true, error = null)

        viewModelScope.launch {
            try {
                // GET /api/profiles/mentor/{id}
                val response = profileApi.getProfile("mentor", userId)

                if (response.isSuccessful) {
                    val body = response.body()
                    _state.value = _state.value.copy(
                        loading = false,
                        profile = body
                    )
                } else {
                    _state.value = _state.value.copy(
                        loading = false,
                        error = "HTTP ${response.code()}: Error obteniendo perfil"
                    )
                }

            } catch (e: Exception) {
                _state.value = MentorProfileState(
                    loading = false,
                    error = e.message ?: "Error cargando perfil"
                )
            }
        }
    }

    // Opcional: para recargar desde la UI si lo necesitas
    fun refresh(userId: Long) = loadMentorProfile(userId)
}
