package com.example.machteacher.ui.sessions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.api.SessionApi
import com.example.machteacher.dao.SessionDao
import com.example.machteacher.dto.RatingRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionDetailUi(
    val loading: Boolean = true,
    val exists: Boolean = true,
    val mentorName: String = "",
    val subject: String = "",
    val whenText: String = "",
    val durationText: String = "1 hora",
    val modeText: String = "",
    val deleting: Boolean = false,
    val deleted: Boolean = false,
    val showRating: Boolean = false,
    val rating: Int = 0,
    val comment: String = "",
    val submittingRating: Boolean = false,
    val finished: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val dao: SessionDao,
    private val sessionApi: SessionApi,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<Long>("id") ?: 0L

    private data class Extras(
        val deleting: Boolean = false,
        val deleted: Boolean = false,
        val showRating: Boolean = false,
        val rating: Int = 0,
        val comment: String = "",
        val submittingRating: Boolean = false,
        val finished: Boolean = false,
        val error: String? = null
    )

    private val _extras = MutableStateFlow(Extras())
    private val sessionFlow = dao.observeById(sessionId)

    val state: StateFlow<SessionDetailUi> =
        combine(sessionFlow, _extras) { e, ex ->
            if (e == null) {
                SessionDetailUi(
                    loading = false,
                    exists = false,
                    deleting = ex.deleting,
                    deleted = ex.deleted,
                    showRating = ex.showRating,
                    rating = ex.rating,
                    comment = ex.comment,
                    submittingRating = ex.submittingRating,
                    finished = ex.finished,
                    error = ex.error
                )
            } else {
                SessionDetailUi(
                    loading = false,
                    exists = true,
                    mentorName = e.mentorName,
                    subject = e.subject,
                    whenText = e.dateTime,
                    modeText = e.modality,
                    deleting = ex.deleting,
                    deleted = ex.deleted,
                    showRating = ex.showRating,
                    rating = ex.rating,
                    comment = ex.comment,
                    submittingRating = ex.submittingRating,
                    finished = ex.finished,
                    error = ex.error
                )
            }
        }
            .onStart { emit(SessionDetailUi(loading = true)) }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                SessionDetailUi(loading = true)
            )

    /* ===================== ELIMINAR ===================== */

    fun deleteSession() {
        if (sessionId == 0L) return

        viewModelScope.launch {
            _extras.update { it.copy(deleting = true, error = null) }

            try {
                val resp = sessionApi.deleteSession(sessionId)

                if (!resp.isSuccessful) {
                    throw IllegalStateException("Backend no borró la sesión (${resp.code()})")
                }

                dao.deleteById(sessionId)

                _extras.update { it.copy(deleting = false, deleted = true) }
            } catch (e: Exception) {
                _extras.update {
                    it.copy(
                        deleting = false,
                        deleted = false,
                        error = e.message ?: "No se pudo eliminar"
                    )
                }
            }
        }
    }


    /* ===================== RATING ===================== */

    fun openRating() { _extras.update { it.copy(showRating = true) } }
    fun closeRating() { _extras.update { it.copy(showRating = false) } }
    fun setRating(stars: Int) { _extras.update { it.copy(rating = stars.coerceIn(0, 5)) } }
    fun setComment(text: String) { _extras.update { it.copy(comment = text) } }

    fun submitRating() {
        val current = _extras.value
        if (sessionId == 0L || current.rating <= 0) {
            _extras.update { it.copy(error = "Selecciona una calificación") }
            return
        }

        viewModelScope.launch {
            _extras.update { it.copy(submittingRating = true, error = null) }
            try {
                sessionApi.rateSession(
                    sessionId,
                    RatingRequest(current.rating, current.comment.ifBlank { null })
                )

                runCatching { dao.deleteById(sessionId) }

                _extras.update { it.copy(submittingRating = false, finished = true, showRating = false) }
            } catch (e: Exception) {
                _extras.update {
                    it.copy(
                        submittingRating = false,
                        error = e.message ?: "No se pudo enviar la calificación"
                    )
                }
            }
        }
    }

    fun dismissError() { _extras.update { it.copy(error = null) } }
}
