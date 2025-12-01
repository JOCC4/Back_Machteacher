// app/src/main/java/com/example/machteacher/ui/booking/BookingViewModel.kt
package com.example.machteacher.ui.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.api.CatalogApi
import com.example.machteacher.dto.SessionRequest
import com.example.machteacher.repository.ProfileRepository
import com.example.machteacher.repository.SessionRepository
import com.example.machteacher.storage.AppDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val catalogApi: CatalogApi,
    private val sessionRepository: SessionRepository,
    private val dataStore: AppDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val mentorId: Long? = savedStateHandle.get("mentorId")
        if (mentorId != null && mentorId != 0L) {
            _uiState.update { it.copy(mentorId = mentorId) }
            loadMentorDetails(mentorId)
        } else {
            _uiState.update { it.copy(error = "ID de mentor no válido") }
        }
    }

    private fun String.norm(): String =
        Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{M}+".toRegex(), "")
            .trim()
            .lowercase()

    private fun String.singularizeEs(): String {
        val n = this
        return when {
            n.endsWith("es") -> n.removeSuffix("es")
            n.endsWith("s") -> n.removeSuffix("s")
            else -> n
        }
    }

    private fun mapModality(ui: String) = when (ui.lowercase()) {
        "online" -> "ONLINE"
        "presencial" -> "PRESENCIAL"
        else -> "ONLINE"
    }

    private fun mapDurationMinutes(label: String) = when (label.trim()) {
        "1 hora" -> 60
        "1.5 horas" -> 90
        "2 horas" -> 120
        "3 horas" -> 180
        else -> 60
    }

    private fun mapDateToIso(dateDdMmYyyy: String): String = try {
        val inFmt = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        val outFmt = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val parsed = inFmt.parse(dateDdMmYyyy)
        outFmt.format(parsed ?: java.util.Date())
    } catch (_: Exception) {
        dateDdMmYyyy
    }

    private fun loadMentorDetails(mentorId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val mentorProfile = profileRepository.getMentorProfile(mentorId)
                val materias = mentorProfile.subjects
                    ?.split(",")
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
                    ?: emptyList()
                val precio = mentorProfile.hourlyRate?.toIntOrNull() ?: 0

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mentorName = mentorProfile.name ?: "Nombre no disponible",
                        subjects = materias,
                        price = precio
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "No se pudo cargar el perfil del mentor"
                    )
                }
            }
        }
    }

    fun updateSubject(subject: String) { _uiState.update { it.copy(selectedSubject = subject) } }
    fun updatePackage(pkg: String) { _uiState.update { it.copy(selectedPackage = pkg) } }
    fun updateMode(mode: String) { _uiState.update { it.copy(selectedMode = mode) } }
    fun updateDate(date: String) { _uiState.update { it.copy(selectedDate = date) } }
    fun updateTime(time: String) { _uiState.update { it.copy(selectedTime = time) } }
    fun updateNotes(notes: String) { _uiState.update { it.copy(notes = notes) } }
    fun updateDuration(label: String) { _uiState.update { it.copy(duration = label) } }
    fun consumeSuccess() { _uiState.update { it.copy(success = false) } }

    fun addToCart() {
        val s = _uiState.value

        if (s.selectedSubject.isNullOrBlank() ||
            s.selectedDate.isBlank() ||
            s.selectedTime.isBlank()
        ) {
            _uiState.update { it.copy(error = "Completa materia, fecha y hora") }
            return
        }

        val item = CartItem(
            mentorName = s.mentorName,
            subject = s.selectedSubject!!,
            packageName = s.selectedPackage,
            date = s.selectedDate,
            time = s.selectedTime,
            duration = s.duration,
            price = s.price,
            mode = s.selectedMode,
            notes = s.notes
        )

        _uiState.update {
            it.copy(
                cart = listOf(item),
                error = null
            )
        }
    }

    fun confirmBooking() {
        val s = _uiState.value
        val item = s.cart.firstOrNull()

        if (s.mentorId == null || item == null) {
            _uiState.update { it.copy(error = "No hay sesión en el carrito") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val subjects = catalogApi.listSubjects()
                val sel = item.subject.norm().singularizeEs()

                val subjectMatch = subjects.firstOrNull { it.name.norm().singularizeEs() == sel }
                    ?: subjects.firstOrNull {
                        val cand = it.name.norm()
                        cand.contains(sel) || sel.contains(cand.singularizeEs())
                    }

                if (subjectMatch == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "La materia '${item.subject}' no existe en el catálogo."
                        )
                    }
                    return@launch
                }

                val subjectId = subjectMatch.id
                val studentId = dataStore.getUserId()
                    ?: throw IllegalStateException("No hay usuario logueado")

                val req = SessionRequest(
                    mentorId = s.mentorId,
                    studentId = studentId,
                    subjectId = subjectId,
                    packageTypeId = null,
                    date = mapDateToIso(item.date),
                    startTime = item.time,
                    durationMinutes = mapDurationMinutes(item.duration),
                    modality = mapModality(item.mode),
                    notes = item.notes.ifBlank { null },
                    priceUsd = item.price.toDouble()
                )

                val sessionId = sessionRepository.createSession(req)

                sessionRepository.startConversation(sessionId)
                sessionRepository.refreshUpcomingSessions()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        cart = emptyList(),
                        success = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Error al crear sesión"
                    )
                }
            }
        }
    }
}
