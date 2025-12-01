// app/src/main/java/com/example/machteacher/ui/sos/SosViewModel.kt
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
    // 🎯 Alumno
    val isSending: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null,
    val isAccepted: Boolean = false,
    val acceptedByName: String? = null,
    val acceptedByEmail: String? = null,
    val hasPendingSos: Boolean = false,
    val showAcceptedPopup: Boolean = false,   // controla directamente el popup

    // 🎯 Mentor (lista SOS)
    val isLoading: Boolean = false,
    val sosList: List<SosResponseDto> = emptyList()
)

@HiltViewModel
class SosViewModel @Inject constructor(
    private val sosRepository: SosRepository
) : ViewModel() {

    companion object {
        // 🧠 Se mantiene mientras el proceso de la app siga vivo
        // y evita repetir el popup de aceptación para el mismo SOS
        private var globalLastHandledAcceptedSosId: Long? = null
    }

    private val _state = MutableStateFlow(SosUiState())
    val state: StateFlow<SosUiState> = _state

    private var lastCreatedSosId: Long? = null
    private var currentStudentId: Long? = null

    /* 🚨 Alumno envía SOS */
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
                        // 👇 SOLO aquí mostramos el mensaje de “SOS enviado”
                        successMessage = "🚨 SOS enviado, espera un mentor disponible",
                        hasPendingSos = true,
                        showAcceptedPopup = false
                    )
                }

                // empezar a chequear si fue aceptado
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

    /* 🔍 Alumno: ver si ya aceptaron el SOS (polling mientras la app está viva) */
    private fun checkIfAccepted() {
        viewModelScope.launch {
            val sosId = lastCreatedSosId ?: return@launch
            val student = currentStudentId ?: return@launch

            repeat(30) {   // ~2 minutos
                delay(5000)
                val list = sosRepository.getSosByStudent(student)
                val mySos = list.firstOrNull { it.id == sosId }

                if (mySos?.status == "ACCEPTED") {
                    val mentorName = mySos.acceptedBy?.fullName ?: "un mentor"

                    // marcar este SOS como ya notificado (global)
                    globalLastHandledAcceptedSosId = mySos.id

                    _state.update {
                        it.copy(
                            isAccepted = true,
                            acceptedByName = mentorName,
                            successMessage = null,
                            hasPendingSos = false,
                            showAcceptedPopup = true   // dispara popup + confeti
                        )
                    }
                    return@launch
                }
            }
        }
    }

    /* 🔍 Alumno: cargar estado SOS al entrar al Home */
    fun loadStudentSos(studentId: Long) {
        viewModelScope.launch {
            try {
                val list = sosRepository.getSosByStudent(studentId)

                // 1) Buscar SOS aceptados NUEVOS (id mayor al último mostrado)
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
                            showAcceptedPopup = true   // 👈 se muestra SOLO una vez por id
                        )
                    }
                    return@launch
                }

                // 2) Si no hay nuevos aceptados, revisar si hay algún PENDING (último creado)
                val latestPending = list
                    .filter { it.status == "PENDING" }
                    .maxByOrNull { it.id }

                if (latestPending != null) {
                    _state.update {
                        it.copy(
                            hasPendingSos = true,
                            // ⛔ OJO: ya NO seteamos successMessage aquí,
                            // así NO se repite el popup de “SOS enviado” al volver al Home
                            isAccepted = false,
                            showAcceptedPopup = false
                        )
                    }
                } else {
                    // 3) Sin SOS activos
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
                // en esta carga inicial podemos ignorar el error silenciosamente
            }
        }
    }

    /* 🧹 Limpiar mensajes/popup (NO resetea globalLastHandledAcceptedSosId) */
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

    /* 🧑‍🏫 Mentor: cargar lista de SOS activos */
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

    /* 🧑‍🏫 Mentor: aceptar un SOS de la lista */
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
