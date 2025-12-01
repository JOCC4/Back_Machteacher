package com.example.machteacher.ui.mysessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.machteacher.model.Session
import com.example.machteacher.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MySessionsUiState(
    val isLoading: Boolean = false,
    val sessions: List<Session> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MySessionsViewModel @Inject constructor(
    private val repository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MySessionsUiState())
    val uiState: StateFlow<MySessionsUiState> = _uiState

    init {
        loadSessions()
    }

    private fun loadSessions() {
        viewModelScope.launch {
            _uiState.value = MySessionsUiState(isLoading = true)
            try {
                repository.refreshUpcomingSessions() // Primero, actualiza desde la red
            } catch (e: Exception) {
                _uiState.value = MySessionsUiState(error = "Error al refrescar las sesiones: ${e.message}")
                // No detenemos el flujo, aún podemos mostrar las sesiones de la BD local
            }

            repository.getUpcomingSessions()
                .catch { e -> 
                    // Este catch es por si el Flow de la BD falla, lo cual es raro
                    _uiState.value = MySessionsUiState(error = "Error al leer de la base de datos: ${e.message}") 
                }
                .collect { sessions ->
                    // Actualizamos la UI con las sesiones de la BD, manteniendo el error si lo hubo
                    _uiState.value = _uiState.value.copy(isLoading = false, sessions = sessions)
                }
        }
    }
}
