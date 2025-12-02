package com.example.machteacher.ui.sos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.dto.SosResponseDto
import com.example.machteacher.repository.SosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SosUiState(

    val isSending: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    val isAccepted: Boolean = false,
    val acceptedByName: String? = null,
    val acceptedByEmail: String? = null,
    val hasPendingSos: Boolean = false,
    val showAcceptedPopup: Boolean = false,


    val isLoading: Boolean = false,
    val sosList: List<SosResponseDto> = emptyList()
)

@HiltViewModel
class SosViewModel @Inject constructor(
    private val sosRepository: SosRepository
) : ViewModel() {

    companion object {

        private var globalLastHandledAcceptedSosId: Long? = null
    }

    private val _state = MutableStateFlow(SosUiState())
    val state: StateFlow<SosUiState> = _state

    private var lastCreatedSosId: Long? = null
    private var currentStudentId: Long? = null


    fun sendSosAsStudent(studentId: Long, subject: String, message: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSending = true,
                    error = null,
                    successMessage = null,
                    isAccepted = false,
                    acceptedByName = null,
                    acceptedByEmail = null,
                    showAcceptedPopup = false
                )
            }

            try {
                val dto = sosRepository.sendSos(studentId, subject, message)

                currentStudentId = studentId
                lastCreatedSosId = dto.id

                _state.update {
                    it.copy(
                        isSending = false,

                        successMessage = "🚨 SOS enviado, espera un mentor disponible",
                        hasPendingSos = true,
                        showAcceptedPopup = false
                    )
                }


                checkIfAccepted()

            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isSending = false,
                        error = "Error al enviar SOS",
                        showAcceptedPopup = false
                    )
                }
            }
        }
    }


    private fun checkIfAccepted() {
        viewModelScope.launch {
            val sosId = lastCreatedSosId ?: return@launch
            val student = currentStudentId ?: return@launch

            repeat(30) {
                delay(5000)
                val list = sosRepository.getSosByStudent(student)
                val mySos = list.firstOrNull { it.id == sosId }

                if (mySos?.status == "ACCEPTED") {
                    val mentorName = mySos.acceptedBy?.fullName ?: "un mentor"


                    globalLastHandledAcceptedSosId = mySos.id

                    _state.update {
                        it.copy(
                            isAccepted = true,
                            acceptedByName = mentorName,
                            successMessage = null,
                            hasPendingSos = false,
                            showAcceptedPopup = true
                        )
                    }
                    return@launch
                }
            }
        }
    }


    fun loadStudentSos(studentId: Long) {
        viewModelScope.launch {
            try {
                val list = sosRepository.getSosByStudent(studentId)


                val newAccepted = list
                    .filter { it.status == "ACCEPTED" }
                    .filter { globalLastHandledAcceptedSosId == null || it.id > globalLastHandledAcceptedSosId!! }
                    .maxByOrNull { it.id }

                if (newAccepted != null) {
                    globalLastHandledAcceptedSosId = newAccepted.id

                    _state.update {
                        it.copy(
                            isAccepted = true,
                            acceptedByName = newAccepted.acceptedBy?.fullName ?: "un mentor",
                            successMessage = null,
                            hasPendingSos = false,
                            showAcceptedPopup = true
                        )
                    }
                    return@launch
                }


                val latestPending = list
                    .filter { it.status == "PENDING" }
                    .maxByOrNull { it.id }

                if (latestPending != null) {
                    _state.update {
                        it.copy(
                            hasPendingSos = true,
                            isAccepted = false,
                            showAcceptedPopup = false
                        )
                    }
                } else {

                    _state.update {
                        it.copy(
                            hasPendingSos = false,
                            isAccepted = false,
                            successMessage = null,
                            showAcceptedPopup = false
                        )
                    }
                }

            } catch (_: Exception) {

            }
        }
    }


    fun clearMessages() {
        _state.update {
            it.copy(
                successMessage = null,
                error = null,
                isAccepted = false,
                acceptedByName = null,
                acceptedByEmail = null,
                showAcceptedPopup = false
            )
        }
    }


    fun loadActiveSosForMentor(mentorId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            try {
                val list = sosRepository.getActiveSos()

                _state.update {
                    it.copy(
                        isLoading = false,
                        sosList = list,
                        hasPendingSos = list.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error cargando SOS",
                        sosList = emptyList(),
                        hasPendingSos = false
                    )
                }
            }
        }
    }


    fun acceptSos(sosId: Long, mentorId: Long) {
        viewModelScope.launch {
            try {
                val dto = sosRepository.acceptSos(sosId, mentorId)

                _state.update { current ->
                    val updatedList = current.sosList.filterNot { it.id == sosId }

                    current.copy(
                        sosList = updatedList,
                        successMessage = "Has aceptado el SOS de ${dto.student?.fullName ?: "un alumno"}",
                        hasPendingSos = updatedList.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error al aceptar SOS")
                }
            }
        }
    }
}
